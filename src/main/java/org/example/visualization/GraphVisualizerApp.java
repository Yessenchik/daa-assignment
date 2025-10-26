package org.example.visualization;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.algorithm.*;
import org.example.model.*;
import org.example.util.*;
import java.util.*;

public class GraphVisualizerApp extends Application {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final int NODE_RADIUS = 20;

    private List<GraphDisplayData> graphDataList;
    private int currentGraphIndex = 0;
    private Canvas canvas;
    private Label infoLabel;
    private Label statusLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            loadGraphData();

            BorderPane root = new BorderPane();
            root.setPadding(new Insets(10));

            canvas = new Canvas(WIDTH, HEIGHT);
            root.setCenter(canvas);

            VBox topPanel = createTopPanel();
            root.setTop(topPanel);

            HBox bottomPanel = createBottomPanel();
            root.setBottom(bottomPanel);

            Scene scene = new Scene(root, WIDTH + 20, HEIGHT + 150);
            primaryStage.setTitle("City Transportation Network - MST Visualization");
            primaryStage.setScene(scene);
            primaryStage.show();

            drawCurrentGraph();

        } catch (Exception e) {
            showError("Failed to load data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createTopPanel() {
        VBox topPanel = new VBox(10);
        topPanel.setPadding(new Insets(10));

        infoLabel = new Label();
        infoLabel.setFont(new Font("Arial", 16));
        infoLabel.setStyle("-fx-font-weight: bold;");

        statusLabel = new Label();
        statusLabel.setFont(new Font("Arial", 14));

        topPanel.getChildren().addAll(infoLabel, statusLabel);
        return topPanel;
    }

    private HBox createBottomPanel() {
        HBox bottomPanel = new HBox(20);
        bottomPanel.setPadding(new Insets(15));
        bottomPanel.setAlignment(Pos.CENTER);

        Button prevButton = new Button("◀ Previous");
        prevButton.setPrefWidth(150);
        prevButton.setPrefHeight(40);
        prevButton.setStyle("-fx-font-size: 14px;");
        prevButton.setOnAction(e -> navigateGraph(-1));

        Button nextButton = new Button("Next ▶");
        nextButton.setPrefWidth(150);
        nextButton.setPrefHeight(40);
        nextButton.setStyle("-fx-font-size: 14px;");
        nextButton.setOnAction(e -> navigateGraph(1));

        bottomPanel.getChildren().addAll(prevButton, nextButton);

        return bottomPanel;
    }

    private void loadGraphData() throws Exception {
        String inputFile = "src/main/resources/input.json";

        if (!new java.io.File(inputFile).exists()) {
            System.out.println("Generating dataset...");
            GraphGenerator generator = new GraphGenerator();
            InputData inputData = generator.generateCompleteDataset();
            JSONHandler jsonHandler = new JSONHandler();
            jsonHandler.writeInput(inputFile, inputData);
        }

        JSONHandler jsonHandler = new JSONHandler();
        InputData inputData = jsonHandler.readInput(inputFile);

        PrimAlgorithm primAlgorithm = new PrimAlgorithm();
        KruskalAlgorithm kruskalAlgorithm = new KruskalAlgorithm();

        graphDataList = new ArrayList<>();

        for (InputData.GraphData graphData : inputData.getGraphs()) {
            Graph graph = jsonHandler.createGraph(graphData);
            MSTResult primResult = primAlgorithm.findMST(graph);
            MSTResult kruskalResult = kruskalAlgorithm.findMST(graph);

            graphDataList.add(new GraphDisplayData(
                    graphData.getId(),
                    graph,
                    primResult,
                    kruskalResult
            ));
        }
    }

    private void navigateGraph(int direction) {
        currentGraphIndex += direction;
        if (currentGraphIndex < 0) {
            currentGraphIndex = graphDataList.size() - 1;
        } else if (currentGraphIndex >= graphDataList.size()) {
            currentGraphIndex = 0;
        }
        drawCurrentGraph();
    }

    private void drawCurrentGraph() {
        GraphDisplayData data = graphDataList.get(currentGraphIndex);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        updateInfoLabels(data);

        Map<String, Point> positions = calculateNodePositions(data.graph);
        Set<String> mstEdgeSet = createEdgeSet(data.primResult.getEdges());

        drawEdges(gc, data.graph, positions, mstEdgeSet);
        drawNodes(gc, data.graph, positions);
        drawLegend(gc);
    }

    private void updateInfoLabels(GraphDisplayData data) {
        infoLabel.setText(String.format("Graph ID: %d | Nodes: %d | Edges: %d | MST Cost: %d",
                data.id,
                data.graph.getNodeCount(),
                data.graph.getEdgeCount(),
                data.primResult.getTotalCost()
        ));

        statusLabel.setText(String.format(
                "Prim's: %dms (%d ops) | Kruskal's: %dms (%d ops) | Graph %d of %d",
                data.primResult.getExecutionTimeMs(),
                data.primResult.getOperationCount(),
                data.kruskalResult.getExecutionTimeMs(),
                data.kruskalResult.getOperationCount(),
                currentGraphIndex + 1,
                graphDataList.size()
        ));
    }

    private void drawEdges(GraphicsContext gc, Graph graph, Map<String, Point> positions, Set<String> mstEdgeSet) {
        gc.setFont(new Font("Arial", 10));
        for (Edge edge : graph.getEdges()) {
            Point p1 = positions.get(edge.getFrom());
            Point p2 = positions.get(edge.getTo());

            if (p1 == null || p2 == null) {
                continue;
            }

            String edgeKey = createEdgeKey(edge.getFrom(), edge.getTo());
            if (mstEdgeSet.contains(edgeKey)) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(3);
            } else {
                gc.setStroke(Color.LIGHTGRAY);
                gc.setLineWidth(1);
            }

            gc.strokeLine(p1.x, p1.y, p2.x, p2.y);

            double midX = (p1.x + p2.x) / 2;
            double midY = (p1.y + p2.y) / 2;
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(edge.getWeight()), midX, midY);
        }
    }

    private void drawNodes(GraphicsContext gc, Graph graph, Map<String, Point> positions) {
        for (String node : graph.getNodes()) {
            Point p = positions.get(node);
            if (p == null) {
                continue;
            }

            gc.setFill(Color.LIGHTBLUE);
            gc.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
            gc.setStroke(Color.DARKBLUE);
            gc.setLineWidth(2);
            gc.strokeOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Arial", 12));
            double textWidth = node.length() * 6;
            gc.fillText(node, p.x - textWidth / 2, p.y + 5);
        }
    }

    private void drawLegend(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(20, HEIGHT - 80, 200, 60);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(20, HEIGHT - 80, 200, 60);

        gc.setStroke(Color.RED);
        gc.setLineWidth(3);
        gc.strokeLine(30, HEIGHT - 60, 70, HEIGHT - 60);
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 12));
        gc.fillText("MST Edges", 80, HEIGHT - 55);

        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);
        gc.strokeLine(30, HEIGHT - 35, 70, HEIGHT - 35);
        gc.fillText("Other Edges", 80, HEIGHT - 30);
    }

    private Map<String, Point> calculateNodePositions(Graph graph) {
        Map<String, Point> positions = new HashMap<>();
        List<String> nodes = graph.getNodes();
        int n = nodes.size();

        if (n == 0) {
            return positions;
        }

        if (n <= 10) {
            double radius = Math.min(WIDTH, HEIGHT) / 3.0;
            double centerX = WIDTH / 2.0;
            double centerY = HEIGHT / 2.0;

            for (int i = 0; i < n; i++) {
                double angle = 2 * Math.PI * i / n - Math.PI / 2;
                double x = centerX + radius * Math.cos(angle);
                double y = centerY + radius * Math.sin(angle);
                positions.put(nodes.get(i), new Point(x, y));
            }
        } else {
            Random random = new Random(42);
            int margin = 100;
            for (String node : nodes) {
                double x = margin + random.nextDouble() * (WIDTH - 2 * margin);
                double y = margin + random.nextDouble() * (HEIGHT - 2 * margin);
                positions.put(node, new Point(x, y));
            }

            forceDirectedLayout(graph, positions, 100);
        }

        return positions;
    }

    private void forceDirectedLayout(Graph graph, Map<String, Point> positions, int iterations) {
        double k = Math.sqrt((WIDTH * HEIGHT) / Math.max(graph.getNodeCount(), 1));

        for (int iter = 0; iter < iterations; iter++) {
            Map<String, Point> forces = new HashMap<>();
            for (String node : graph.getNodes()) {
                forces.put(node, new Point(0, 0));
            }

            for (String v : graph.getNodes()) {
                for (String u : graph.getNodes()) {
                    if (!v.equals(u)) {
                        Point pv = positions.get(v);
                        Point pu = positions.get(u);
                        if (pv == null || pu == null) {
                            continue;
                        }
                        double dx = pv.x - pu.x;
                        double dy = pv.y - pu.y;
                        double distance = Math.max(Math.sqrt(dx * dx + dy * dy), 0.01);
                        double force = k * k / distance;
                        Point fv = forces.get(v);
                        fv.x += (dx / distance) * force;
                        fv.y += (dy / distance) * force;
                    }
                }
            }

            for (Edge edge : graph.getEdges()) {
                Point p1 = positions.get(edge.getFrom());
                Point p2 = positions.get(edge.getTo());
                if (p1 == null || p2 == null) {
                    continue;
                }
                double dx = p2.x - p1.x;
                double dy = p2.y - p1.y;
                double distance = Math.max(Math.sqrt(dx * dx + dy * dy), 0.01);
                double force = distance * distance / k;

                Point f1 = forces.get(edge.getFrom());
                Point f2 = forces.get(edge.getTo());
                if (f1 != null && f2 != null) {
                    f1.x += (dx / distance) * force;
                    f1.y += (dy / distance) * force;
                    f2.x -= (dx / distance) * force;
                    f2.y -= (dy / distance) * force;
                }
            }

            double temp = 0.1 * (1 - (double) iter / iterations);
            for (String node : graph.getNodes()) {
                Point p = positions.get(node);
                Point f = forces.get(node);
                if (p == null || f == null) {
                    continue;
                }
                double magnitude = Math.sqrt(f.x * f.x + f.y * f.y);
                if (magnitude > 0) {
                    p.x += (f.x / magnitude) * Math.min(magnitude, temp);
                    p.y += (f.y / magnitude) * Math.min(magnitude, temp);
                    p.x = Math.max(50, Math.min(WIDTH - 50, p.x));
                    p.y = Math.max(50, Math.min(HEIGHT - 50, p.y));
                }
            }
        }
    }

    private Set<String> createEdgeSet(List<Edge> edges) {
        Set<String> edgeSet = new HashSet<>();
        for (Edge edge : edges) {
            edgeSet.add(createEdgeKey(edge.getFrom(), edge.getTo()));
        }
        return edgeSet;
    }

    private String createEdgeKey(String from, String to) {
        return from.compareTo(to) < 0 ? from + "-" + to : to + "-" + from;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class Point {
        double x, y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class GraphDisplayData {
        int id;
        Graph graph;
        MSTResult primResult;
        MSTResult kruskalResult;

        GraphDisplayData(int id, Graph graph, MSTResult primResult, MSTResult kruskalResult) {
            this.id = id;
            this.graph = graph;
            this.primResult = primResult;
            this.kruskalResult = kruskalResult;
        }
    }
}
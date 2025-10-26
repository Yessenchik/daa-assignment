package org.example.visualization;

import org.example.model.*;
import org.example.algorithm.MSTResult;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class GraphVisualizer {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 900;
    private static final int NODE_RADIUS = 20;

    public void visualizeGraph(Graph graph, MSTResult primResult, MSTResult kruskalResult,
                               int graphId, String outputDir) {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                Canvas canvas = new Canvas(WIDTH, HEIGHT);
                GraphicsContext gc = canvas.getGraphicsContext2D();

                drawGraph(gc, graph, primResult, kruskalResult);

                WritableImage image = canvas.snapshot(null, null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

                File outputFile = new File(outputDir + "/graph_" + graphId + ".png");
                outputFile.getParentFile().mkdirs();
                ImageIO.write(bufferedImage, "png", outputFile);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void drawGraph(GraphicsContext gc, Graph graph, MSTResult primResult,
                           MSTResult kruskalResult) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        Map<String, Point> positions = calculateNodePositions(graph);
        Set<String> mstEdgeSet = createEdgeSet(primResult.getEdges());

        gc.setFont(new Font("Arial", 10));
        for (Edge edge : graph.getEdges()) {
            Point p1 = positions.get(edge.getFrom());
            Point p2 = positions.get(edge.getTo());

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

        for (String node : graph.getNodes()) {
            Point p = positions.get(node);
            gc.setFill(Color.LIGHTBLUE);
            gc.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Arial", 12));
            gc.fillText(node, p.x - 5, p.y + 5);
        }

        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 14));
        gc.fillText("MST Cost: " + primResult.getTotalCost(), 20, 30);
        gc.fillText("Nodes: " + graph.getNodeCount() + ", Edges: " + graph.getEdgeCount(), 20, 50);
    }

    private Map<String, Point> calculateNodePositions(Graph graph) {
        Map<String, Point> positions = new HashMap<>();
        List<String> nodes = graph.getNodes();
        int n = nodes.size();

        if (n <= 10) {
            double radius = Math.min(WIDTH, HEIGHT) / 3.0;
            double centerX = WIDTH / 2.0;
            double centerY = HEIGHT / 2.0;

            for (int i = 0; i < n; i++) {
                double angle = 2 * Math.PI * i / n;
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
        double k = Math.sqrt((WIDTH * HEIGHT) / graph.getNodeCount());

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
                double dx = p2.x - p1.x;
                double dy = p2.y - p1.y;
                double distance = Math.max(Math.sqrt(dx * dx + dy * dy), 0.01);
                double force = distance * distance / k;

                Point f1 = forces.get(edge.getFrom());
                Point f2 = forces.get(edge.getTo());
                f1.x += (dx / distance) * force;
                f1.y += (dy / distance) * force;
                f2.x -= (dx / distance) * force;
                f2.y -= (dy / distance) * force;
            }

            double temp = 0.1 * (1 - (double) iter / iterations);
            for (String node : graph.getNodes()) {
                Point p = positions.get(node);
                Point f = forces.get(node);
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

    private static class Point {
        double x, y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void initJavaFX() {
        new JFXPanel();
    }
}
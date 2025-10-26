package org.example;

import org.example.algorithm.*;
import org.example.model.*;
import org.example.util.*;
import org.example.visualization.GraphVisualizer;
import org.example.visualization.GraphVisualizerApp;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--gui")) {
            GraphVisualizerApp.main(args);
            return;
        }

        try {
            GraphVisualizer.initJavaFX();

            System.out.println("=== City Transportation Network Optimization ===\n");

            String inputFile = args.length > 0 ? args[0] : "src/main/resources/input.json";
            String outputFile = args.length > 1 ? args[1] : "output/output.json";
            String graphOutputDir = args.length > 2 ? args[2] : "output/graphs";

            if (!new java.io.File(inputFile).exists()) {
                System.out.println("Input file not found. Generating dataset...");
                generateDataset(inputFile);
            }

            JSONHandler jsonHandler = new JSONHandler();
            InputData inputData = jsonHandler.readInput(inputFile);

            PrimAlgorithm primAlgorithm = new PrimAlgorithm();
            KruskalAlgorithm kruskalAlgorithm = new KruskalAlgorithm();
            GraphVisualizer visualizer = new GraphVisualizer();

            OutputData outputData = new OutputData();
            List<OutputData.GraphResult> results = new ArrayList<>();

            System.out.println("Processing " + inputData.getGraphs().size() + " graphs...\n");

            for (InputData.GraphData graphData : inputData.getGraphs()) {
                System.out.println("Processing Graph ID: " + graphData.getId() +
                        " (Nodes: " + graphData.getNodes().size() +
                        ", Edges: " + graphData.getEdges().size() + ")");

                Graph graph = jsonHandler.createGraph(graphData);

                if (!graph.isConnected()) {
                    System.out.println("  Warning: Graph is disconnected!");
                }

                MSTResult primResult = primAlgorithm.findMST(graph);
                MSTResult kruskalResult = kruskalAlgorithm.findMST(graph);

                System.out.println("  Prim's   -> Cost: " + primResult.getTotalCost() +
                        ", Time: " + primResult.getExecutionTimeMs() + "ms" +
                        ", Operations: " + primResult.getOperationCount());
                System.out.println("  Kruskal's -> Cost: " + kruskalResult.getTotalCost() +
                        ", Time: " + kruskalResult.getExecutionTimeMs() + "ms" +
                        ", Operations: " + kruskalResult.getOperationCount());

                OutputData.GraphResult result = createGraphResult(graphData, primResult, kruskalResult);
                results.add(result);

                visualizer.visualizeGraph(graph, primResult, kruskalResult,
                        graphData.getId(), graphOutputDir);

                System.out.println("  Graph visualization saved.\n");
            }

            outputData.setResults(results);
            jsonHandler.writeOutput(outputFile, outputData);

            System.out.println("=== Processing Complete ===");
            System.out.println("Output saved to: " + outputFile);
            System.out.println("Visualizations saved to: " + graphOutputDir);

            System.exit(0);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void generateDataset(String filename) throws IOException {
        GraphGenerator generator = new GraphGenerator();
        InputData inputData = generator.generateCompleteDataset();
        JSONHandler jsonHandler = new JSONHandler();
        jsonHandler.writeInput(filename, inputData);
        System.out.println("Dataset generated: " + filename + "\n");
    }

    private static OutputData.GraphResult createGraphResult(InputData.GraphData graphData,
                                                            MSTResult primResult,
                                                            MSTResult kruskalResult) {
        OutputData.GraphResult result = new OutputData.GraphResult();
        result.setId(graphData.getId());
        result.setNodes(graphData.getNodes());

        List<OutputData.EdgeData> edges = new ArrayList<>();
        for (InputData.EdgeData e : graphData.getEdges()) {
            edges.add(new OutputData.EdgeData(e.getFrom(), e.getTo(), e.getWeight()));
        }
        result.setEdges(edges);

        result.setPrim(createAlgorithmResult(primResult));
        result.setKruskal(createAlgorithmResult(kruskalResult));

        return result;
    }

    private static OutputData.AlgorithmResult createAlgorithmResult(MSTResult mstResult) {
        OutputData.AlgorithmResult result = new OutputData.AlgorithmResult();

        List<OutputData.EdgeData> mstEdges = new ArrayList<>();
        for (Edge e : mstResult.getEdges()) {
            mstEdges.add(new OutputData.EdgeData(e.getFrom(), e.getTo(), e.getWeight()));
        }

        result.setMstEdges(mstEdges);
        result.setTotalCost(mstResult.getTotalCost());
        result.setVertexCount(mstResult.getVertexCount());
        result.setOriginalEdgeCount(mstResult.getEdgeCount());
        result.setOperationCount(mstResult.getOperationCount());
        result.setExecutionTimeMs(mstResult.getExecutionTimeMs());

        return result;
    }
}
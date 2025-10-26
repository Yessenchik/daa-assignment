package org.example.util;

import org.example.model.InputData;
import java.util.*;

public class GraphGenerator {
    private final Random random;

    public GraphGenerator() {
        this.random = new Random(42);
    }

    public InputData generateCompleteDataset() {
        InputData inputData = new InputData();
        List<InputData.GraphData> graphs = new ArrayList<>();

        graphs.addAll(generateSmallGraphs());
        graphs.addAll(generateMediumGraphs());
        graphs.addAll(generateLargeGraphs());
        graphs.addAll(generateExtraLargeGraphs());

        inputData.setGraphs(graphs);
        return inputData;
    }

    private List<InputData.GraphData> generateSmallGraphs() {
        List<InputData.GraphData> graphs = new ArrayList<>();
        int[] sizes = {5, 10, 15, 20, 25};
        for (int i = 0; i < 5; i++) {
            graphs.add(generateGraph(i + 1, sizes[i]));
        }
        return graphs;
    }

    private List<InputData.GraphData> generateMediumGraphs() {
        List<InputData.GraphData> graphs = new ArrayList<>();
        int[] sizes = {30, 35, 40, 45, 50, 55, 60, 65, 70, 75};
        for (int i = 0; i < 10; i++) {
            graphs.add(generateGraph(i + 6, sizes[i]));
        }
        return graphs;
    }

    private List<InputData.GraphData> generateLargeGraphs() {
        List<InputData.GraphData> graphs = new ArrayList<>();
        int[] sizes = {80, 90, 100, 110, 120, 130, 140, 150, 160, 170};
        for (int i = 0; i < 10; i++) {
            graphs.add(generateGraph(i + 16, sizes[i]));
        }
        return graphs;
    }

    private List<InputData.GraphData> generateExtraLargeGraphs() {
        List<InputData.GraphData> graphs = new ArrayList<>();
        int[] sizes = {200, 250, 300};
        for (int i = 0; i < 3; i++) {
            graphs.add(generateGraph(i + 26, sizes[i]));
        }
        return graphs;
    }

    private InputData.GraphData generateGraph(int id, int nodeCount) {
        InputData.GraphData graph = new InputData.GraphData();
        graph.setId(id);

        List<String> nodes = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            nodes.add("N" + i);
        }
        graph.setNodes(nodes);

        List<InputData.EdgeData> edges = new ArrayList<>();
        Set<String> edgeSet = new HashSet<>();

        for (int i = 1; i < nodeCount; i++) {
            int parent = random.nextInt(i);
            addEdge(edges, edgeSet, nodes.get(parent), nodes.get(i));
        }

        int additionalEdges = Math.min(nodeCount * 2, (nodeCount * (nodeCount - 1)) / 2 - edges.size());
        for (int i = 0; i < additionalEdges; i++) {
            int from = random.nextInt(nodeCount);
            int to = random.nextInt(nodeCount);
            if (from != to) {
                addEdge(edges, edgeSet, nodes.get(from), nodes.get(to));
            }
        }

        graph.setEdges(edges);
        return graph;
    }

    private void addEdge(List<InputData.EdgeData> edges, Set<String> edgeSet,
                         String from, String to) {
        String edgeKey = from.compareTo(to) < 0 ? from + "-" + to : to + "-" + from;
        if (!edgeSet.contains(edgeKey)) {
            edgeSet.add(edgeKey);
            InputData.EdgeData edge = new InputData.EdgeData();
            edge.setFrom(from);
            edge.setTo(to);
            edge.setWeight(random.nextInt(100) + 1);
            edges.add(edge);
        }
    }
}
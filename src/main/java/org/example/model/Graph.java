package org.example.model;

import java.util.*;

public class Graph {
    private final Map<String, Integer> nodeIndexMap;
    private final List<String> nodes;
    private final List<Edge> edges;
    private final int[][] adjacencyMatrix;

    public Graph(List<String> nodes, List<Edge> edges) {
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>(edges);
        this.nodeIndexMap = new HashMap<>();

        for (int i = 0; i < nodes.size(); i++) {
            nodeIndexMap.put(nodes.get(i), i);
        }

        this.adjacencyMatrix = new int[nodes.size()][nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            Arrays.fill(adjacencyMatrix[i], Integer.MAX_VALUE);
            adjacencyMatrix[i][i] = 0;
        }

        for (Edge edge : edges) {
            int from = nodeIndexMap.get(edge.getFrom());
            int to = nodeIndexMap.get(edge.getTo());
            adjacencyMatrix[from][to] = edge.getWeight();
            adjacencyMatrix[to][from] = edge.getWeight();
        }
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }

    public List<String> getNodes() {
        return new ArrayList<>(nodes);
    }

    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    public int getNodeIndex(String node) {
        return nodeIndexMap.getOrDefault(node, -1);
    }

    public String getNodeName(int index) {
        return nodes.get(index);
    }

    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public List<Edge> getAdjacentEdges(String node) {
        List<Edge> adjacent = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getFrom().equals(node) || edge.getTo().equals(node)) {
                adjacent.add(edge);
            }
        }
        return adjacent;
    }

    public boolean isConnected() {
        if (nodes.isEmpty()) return true;

        boolean[] visited = new boolean[nodes.size()];
        dfs(0, visited);

        for (boolean v : visited) {
            if (!v) return false;
        }
        return true;
    }

    private void dfs(int node, boolean[] visited) {
        visited[node] = true;
        for (int i = 0; i < nodes.size(); i++) {
            if (!visited[i] && adjacencyMatrix[node][i] != Integer.MAX_VALUE) {
                dfs(i, visited);
            }
        }
    }
}
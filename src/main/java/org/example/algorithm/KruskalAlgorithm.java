package org.example.algorithm;

import org.example.model.Edge;
import org.example.model.Graph;
import java.util.*;

public class KruskalAlgorithm {

    private static class UnionFind {
        private final Map<String, String> parent;
        private final Map<String, Integer> rank;

        public UnionFind(List<String> nodes) {
            parent = new HashMap<>();
            rank = new HashMap<>();
            for (String node : nodes) {
                parent.put(node, node);
                rank.put(node, 0);
            }
        }

        public String find(String node) {
            if (!parent.get(node).equals(node)) {
                parent.put(node, find(parent.get(node)));
            }
            return parent.get(node);
        }

        public boolean union(String node1, String node2) {
            String root1 = find(node1);
            String root2 = find(node2);

            if (root1.equals(root2)) {
                return false;
            }

            int rank1 = rank.get(root1);
            int rank2 = rank.get(root2);

            if (rank1 < rank2) {
                parent.put(root1, root2);
            } else if (rank1 > rank2) {
                parent.put(root2, root1);
            } else {
                parent.put(root2, root1);
                rank.put(root1, rank1 + 1);
            }

            return true;
        }
    }

    public MSTResult findMST(Graph graph) {
        long startTime = System.nanoTime();
        int operationCount = 0;

        if (graph.getNodeCount() == 0) {
            return new MSTResult(new ArrayList<>(), 0, 0, 0, 0, 0);
        }

        List<Edge> edges = new ArrayList<>(graph.getEdges());
        Collections.sort(edges);
        operationCount += edges.size();

        UnionFind uf = new UnionFind(graph.getNodes());
        List<Edge> mstEdges = new ArrayList<>();
        int totalCost = 0;

        for (Edge edge : edges) {
            operationCount++;
            if (uf.union(edge.getFrom(), edge.getTo())) {
                mstEdges.add(edge);
                totalCost += edge.getWeight();

                if (mstEdges.size() == graph.getNodeCount() - 1) {
                    break;
                }
            }
        }

        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1_000_000;

        return new MSTResult(mstEdges, totalCost, executionTime, operationCount,
                graph.getNodeCount(), graph.getEdgeCount());
    }
}
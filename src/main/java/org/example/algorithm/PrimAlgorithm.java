package org.example.algorithm;

import org.example.model.Edge;
import org.example.model.Graph;
import java.util.*;

public class PrimAlgorithm {

    public MSTResult findMST(Graph graph) {
        long startTime = System.nanoTime();
        int operationCount = 0;

        if (graph.getNodeCount() == 0) {
            return new MSTResult(new ArrayList<>(), 0, 0, 0, 0, 0);
        }

        List<Edge> mstEdges = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>();

        String startNode = graph.getNodes().get(0);
        visited.add(startNode);
        pq.addAll(graph.getAdjacentEdges(startNode));

        int totalCost = 0;

        while (!pq.isEmpty() && mstEdges.size() < graph.getNodeCount() - 1) {
            Edge edge = pq.poll();
            operationCount++;

            String nextNode = null;
            if (visited.contains(edge.getFrom()) && !visited.contains(edge.getTo())) {
                nextNode = edge.getTo();
            } else if (visited.contains(edge.getTo()) && !visited.contains(edge.getFrom())) {
                nextNode = edge.getFrom();
            }

            if (nextNode != null) {
                visited.add(nextNode);
                mstEdges.add(edge);
                totalCost += edge.getWeight();

                for (Edge adjacentEdge : graph.getAdjacentEdges(nextNode)) {
                    operationCount++;
                    if (!visited.contains(adjacentEdge.getFrom()) ||
                            !visited.contains(adjacentEdge.getTo())) {
                        pq.offer(adjacentEdge);
                    }
                }
            }
        }

        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1_000_000;

        return new MSTResult(mstEdges, totalCost, executionTime, operationCount,
                graph.getNodeCount(), graph.getEdgeCount());
    }
}
package org.example.algorithm;

import org.example.model.Edge;
import java.util.List;

public class MSTResult {
    private final List<Edge> edges;
    private final int totalCost;
    private final long executionTimeMs;
    private final int operationCount;
    private final int vertexCount;
    private final int edgeCount;

    public MSTResult(List<Edge> edges, int totalCost, long executionTimeMs,
                     int operationCount, int vertexCount, int edgeCount) {
        this.edges = edges;
        this.totalCost = totalCost;
        this.executionTimeMs = executionTimeMs;
        this.operationCount = operationCount;
        this.vertexCount = vertexCount;
        this.edgeCount = edgeCount;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public int getOperationCount() {
        return operationCount;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }
}
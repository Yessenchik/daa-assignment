package org.example.model;

import java.util.List;

public class OutputData {
    private List<GraphResult> results;

    public List<GraphResult> getResults() {
        return results;
    }

    public void setResults(List<GraphResult> results) {
        this.results = results;
    }

    public static class GraphResult {
        private int id;
        private List<String> nodes;
        private List<EdgeData> edges;
        private AlgorithmResult prim;
        private AlgorithmResult kruskal;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<String> getNodes() {
            return nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }

        public List<EdgeData> getEdges() {
            return edges;
        }

        public void setEdges(List<EdgeData> edges) {
            this.edges = edges;
        }

        public AlgorithmResult getPrim() {
            return prim;
        }

        public void setPrim(AlgorithmResult prim) {
            this.prim = prim;
        }

        public AlgorithmResult getKruskal() {
            return kruskal;
        }

        public void setKruskal(AlgorithmResult kruskal) {
            this.kruskal = kruskal;
        }
    }

    public static class AlgorithmResult {
        private List<EdgeData> mstEdges;
        private int totalCost;
        private int vertexCount;
        private int originalEdgeCount;
        private int operationCount;
        private long executionTimeMs;

        public List<EdgeData> getMstEdges() {
            return mstEdges;
        }

        public void setMstEdges(List<EdgeData> mstEdges) {
            this.mstEdges = mstEdges;
        }

        public int getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(int totalCost) {
            this.totalCost = totalCost;
        }

        public int getVertexCount() {
            return vertexCount;
        }

        public void setVertexCount(int vertexCount) {
            this.vertexCount = vertexCount;
        }

        public int getOriginalEdgeCount() {
            return originalEdgeCount;
        }

        public void setOriginalEdgeCount(int originalEdgeCount) {
            this.originalEdgeCount = originalEdgeCount;
        }

        public int getOperationCount() {
            return operationCount;
        }

        public void setOperationCount(int operationCount) {
            this.operationCount = operationCount;
        }

        public long getExecutionTimeMs() {
            return executionTimeMs;
        }

        public void setExecutionTimeMs(long executionTimeMs) {
            this.executionTimeMs = executionTimeMs;
        }
    }

    public static class EdgeData {
        private String from;
        private String to;
        private int weight;

        public EdgeData() {}

        public EdgeData(String from, String to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }
}
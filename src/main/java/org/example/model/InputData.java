package org.example.model;

import java.util.List;

public class InputData {
    private List<GraphData> graphs;

    public List<GraphData> getGraphs() {
        return graphs;
    }

    public void setGraphs(List<GraphData> graphs) {
        this.graphs = graphs;
    }

    public static class GraphData {
        private int id;
        private List<String> nodes;
        private List<EdgeData> edges;

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
    }

    public static class EdgeData {
        private String from;
        private String to;
        private int weight;

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
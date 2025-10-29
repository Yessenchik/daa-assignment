package org.example.algorithm;

import org.example.model.Edge;
import java.util.ArrayList;
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

    // ===== CSV helpers (metrics & optional edges) =====
    /**
     * Header for a compact per-run metrics CSV.
     * Columns: dataset, algorithm, totalCost, timeMs, ops, vertices, edges
     */
    public static String csvMetricsHeader() {
        return "dataset,algorithm,totalCost,timeMs,ops,vertices,edges";
    }

    /**
     * One CSV row with the key metrics for this MST run.
     * @param dataset   logical dataset name (e.g., "grid_100", "random_1e5")
     * @param algorithm algorithm label (e.g., "prim", "kruskal")
     */
    public String toCsvMetricsRow(String dataset, String algorithm) {
        return String.join(",",
                escapeCsv(dataset),
                escapeCsv(algorithm),
                String.valueOf(totalCost),
                String.valueOf(executionTimeMs),
                String.valueOf(operationCount),
                String.valueOf(vertexCount),
                String.valueOf(edgeCount)
        );
    }

    /**
     * Optional: header for per-edge CSV rows if you want to store the actual chosen MST edges.
     * Columns: dataset, algorithm, edge
     * (The "edge" column stores Edge.toString(); adjust if your Edge exposes endpoints/weight getters.)
     */
    public static String csvEdgesHeader() {
        return "dataset,algorithm,edge";
    }

    /**
     * Optional: rows for each edge in the MST. Uses Edge.toString().
     * If you have getters like getFrom()/getTo()/getWeight(), you can format them here instead.
     */
    public java.util.List<String> toCsvEdgeRows(String dataset, String algorithm) {
        java.util.List<String> rows = new ArrayList<>();
        for (Edge e : edges) {
            rows.add(String.join(",",
                    escapeCsv(dataset),
                    escapeCsv(algorithm),
                    escapeCsv(String.valueOf(e))
            ));
        }
        return rows;
    }

    // Minimal CSV escaper (wraps in quotes if needed and doubles internal quotes)
    private static String escapeCsv(String s) {
        if (s == null) return "";
        boolean needsQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String v = s.replace("\"", "\"\"");
        return needsQuotes ? "\"" + v + "\"" : v;
    }
}
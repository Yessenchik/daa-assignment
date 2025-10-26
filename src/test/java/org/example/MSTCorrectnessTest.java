package org.example;

import org.example.algorithm.*;
import org.example.model.*;
import org.example.util.JSONHandler;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class MSTCorrectnessTest {
    private PrimAlgorithm primAlgorithm;
    private KruskalAlgorithm kruskalAlgorithm;

    @BeforeEach
    public void setUp() {
        primAlgorithm = new PrimAlgorithm();
        kruskalAlgorithm = new KruskalAlgorithm();
    }

    @Test
    public void testIdenticalCosts() {
        Graph graph = createSimpleGraph();
        MSTResult primResult = primAlgorithm.findMST(graph);
        MSTResult kruskalResult = kruskalAlgorithm.findMST(graph);

        assertEquals(primResult.getTotalCost(), kruskalResult.getTotalCost(),
                "MST costs must be identical");
    }

    @Test
    public void testEdgeCountEqualsVMinusOne() {
        Graph graph = createSimpleGraph();
        MSTResult primResult = primAlgorithm.findMST(graph);
        MSTResult kruskalResult = kruskalAlgorithm.findMST(graph);

        assertEquals(graph.getNodeCount() - 1, primResult.getEdges().size(),
                "Prim: MST edges should equal V-1");
        assertEquals(graph.getNodeCount() - 1, kruskalResult.getEdges().size(),
                "Kruskal: MST edges should equal V-1");
    }

    @Test
    public void testMSTIsAcyclic() {
        Graph graph = createSimpleGraph();
        MSTResult primResult = primAlgorithm.findMST(graph);
        MSTResult kruskalResult = kruskalAlgorithm.findMST(graph);

        assertTrue(isAcyclic(graph.getNodes(), primResult.getEdges()),
                "Prim: MST must be acyclic");
        assertTrue(isAcyclic(graph.getNodes(), kruskalResult.getEdges()),
                "Kruskal: MST must be acyclic");
    }

    @Test
    public void testMSTConnectsAllVertices() {
        Graph graph = createSimpleGraph();
        MSTResult primResult = primAlgorithm.findMST(graph);
        MSTResult kruskalResult = kruskalAlgorithm.findMST(graph);

        assertTrue(isConnected(graph.getNodes(), primResult.getEdges()),
                "Prim: MST must connect all vertices");
        assertTrue(isConnected(graph.getNodes(), kruskalResult.getEdges()),
                "Kruskal: MST must connect all vertices");
    }

    @Test
    public void testDisconnectedGraph() {
        Graph graph = createDisconnectedGraph();
        MSTResult primResult = primAlgorithm.findMST(graph);
        MSTResult kruskalResult = kruskalAlgorithm.findMST(graph);

        assertTrue(primResult.getEdges().size() < graph.getNodeCount() - 1,
                "Disconnected graph should have fewer edges");
        assertTrue(kruskalResult.getEdges().size() < graph.getNodeCount() - 1,
                "Disconnected graph should have fewer edges");
    }

    @Test
    public void testExecutionTimeNonNegative() {
        Graph graph = createSimpleGraph();
        MSTResult primResult = primAlgorithm.findMST(graph);
        MSTResult kruskalResult = kruskalAlgorithm.findMST(graph);

        assertTrue(primResult.getExecutionTimeMs() >= 0,
                "Execution time must be non-negative");
        assertTrue(kruskalResult.getExecutionTimeMs() >= 0,
                "Execution time must be non-negative");
    }

    @Test
    public void testOperationCountNonNegative() {
        Graph graph = createSimpleGraph();
        MSTResult primResult = primAlgorithm.findMST(graph);
        MSTResult kruskalResult = kruskalAlgorithm.findMST(graph);

        assertTrue(primResult.getOperationCount() > 0,
                "Operation count must be positive");
        assertTrue(kruskalResult.getOperationCount() > 0,
                "Operation count must be positive");
    }

    @Test
    public void testResultsReproducible() {
        Graph graph = createSimpleGraph();

        MSTResult prim1 = primAlgorithm.findMST(graph);
        MSTResult prim2 = primAlgorithm.findMST(graph);

        assertEquals(prim1.getTotalCost(), prim2.getTotalCost(),
                "Results must be reproducible");
    }

    private Graph createSimpleGraph() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E");
        List<Edge> edges = Arrays.asList(
                new Edge("A", "B", 4),
                new Edge("A", "C", 3),
                new Edge("B", "C", 2),
                new Edge("B", "D", 5),
                new Edge("C", "D", 7),
                new Edge("C", "E", 8),
                new Edge("D", "E", 6)
        );
        return new Graph(nodes, edges);
    }

    private Graph createDisconnectedGraph() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges = Arrays.asList(
                new Edge("A", "B", 1),
                new Edge("C", "D", 2)
        );
        return new Graph(nodes, edges);
    }

    private boolean isAcyclic(List<String> nodes, List<Edge> edges) {
        Map<String, List<String>> adj = new HashMap<>();
        for (String node : nodes) {
            adj.put(node, new ArrayList<>());
        }

        for (Edge edge : edges) {
            adj.get(edge.getFrom()).add(edge.getTo());
            adj.get(edge.getTo()).add(edge.getFrom());
        }

        Set<String> visited = new HashSet<>();
        for (String node : nodes) {
            if (!visited.contains(node)) {
                if (hasCycle(node, null, adj, visited)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasCycle(String node, String parent, Map<String, List<String>> adj,
                             Set<String> visited) {
        visited.add(node);
        for (String neighbor : adj.get(node)) {
            if (!visited.contains(neighbor)) {
                if (hasCycle(neighbor, node, adj, visited)) {
                    return true;
                }
            } else if (!neighbor.equals(parent)) {
                return true;
            }
        }
        return false;
    }

    private boolean isConnected(List<String> nodes, List<Edge> edges) {
        if (nodes.isEmpty()) return true;

        Map<String, List<String>> adj = new HashMap<>();
        for (String node : nodes) {
            adj.put(node, new ArrayList<>());
        }

        for (Edge edge : edges) {
            adj.get(edge.getFrom()).add(edge.getTo());
            adj.get(edge.getTo()).add(edge.getFrom());
        }

        Set<String> visited = new HashSet<>();
        dfs(nodes.get(0), adj, visited);

        return visited.size() == nodes.size();
    }

    private void dfs(String node, Map<String, List<String>> adj, Set<String> visited) {
        visited.add(node);
        for (String neighbor : adj.get(node)) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, adj, visited);
            }
        }
    }
}
package com.algorithms;

import com.model.Graph;

import java.util.*;

/**
 * Condensation graph built from strongly connected components.
 * Each node represents an SCC from the original graph.
 * Edges connect components based on original graph edges.
 * The condensation graph is always a DAG.
 */
public class CondensationGraph {
    private final Graph graph;
    private final List<List<Integer>> components;
    private final Map<Integer, Integer> vertexToComponent;
    private final Graph condensation;

    /**
     * Build condensation graph from SCCs.
     * @param originalGraph The original directed graph
     * @param components List of strongly connected components
     */
    public CondensationGraph(Graph originalGraph, List<List<Integer>> components) {
        this.graph = originalGraph;
        this.components = components;
        this.vertexToComponent = new HashMap<>();

        // Map each vertex to its component ID
        for (int i = 0; i < components.size(); i++) {
            for (int vertex : components.get(i)) {
                vertexToComponent.put(vertex, i);
            }
        }

        // Build the condensation graph
        this.condensation = buildCondensation();
    }

    /**
     * Create the condensation DAG.
     * @return Graph where each node is an SCC
     */
    private Graph buildCondensation() {
        int numComponents = components.size();
        Graph cGraph = new Graph(numComponents, true);
        Set<String> addedEdges = new HashSet<>();

        // Check each edge in original graph
        for (int u = 0; u < graph.getNumVertices(); u++) {
            int compU = vertexToComponent.get(u);

            for (Graph.Edge edge : graph.getEdgesFrom(u)) {
                int v = edge.getDestination();
                int compV = vertexToComponent.get(v);

                // Add edge between different components
                if (compU != compV) {
                    String edgeKey = compU + "->" + compV;
                    if (!addedEdges.contains(edgeKey)) {
                        cGraph.addEdge(compU, compV, edge.getWeight());
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }

        return cGraph;
    }

    /**
     * Get the condensation graph.
     * @return DAG of components
     */
    public Graph getCondensationGraph() {
        return condensation;
    }

    /**
     * Get component ID for a vertex.
     * @param vertex Original vertex ID
     * @return Component ID containing this vertex
     */
    public int getComponentId(int vertex) {
        return vertexToComponent.getOrDefault(vertex, -1);
    }

    /**
     * Get all vertices in a component.
     * @param componentId Component ID
     * @return List of vertices in this component
     */
    public List<Integer> getVerticesInComponent(int componentId) {
        if (componentId >= 0 && componentId < components.size()) {
            return new ArrayList<>(components.get(componentId));
        }
        return new ArrayList<>();
    }

    /**
     * Get number of components.
     * @return Number of SCCs
     */
    public int getComponentCount() {
        return components.size();
    }

    /**
     * Get all components.
     * @return List of all SCCs
     */
    public List<List<Integer>> getComponents() {
        return components;
    }

    /**
     * Export condensation graph structure as string.
     * @return Adjacency list representation
     */
    public String exportStructure() {
        StringBuilder sb = new StringBuilder();
        sb.append("Condensation Graph Structure:\n");
        sb.append("Components: ").append(components.size()).append("\n");

        for (int i = 0; i < components.size(); i++) {
            sb.append("Component ").append(i).append(": ");
            sb.append(components.get(i)).append(" (size: ");
            sb.append(components.get(i).size()).append(")\n");

            List<Graph.Edge> edges = condensation.getEdgesFrom(i);
            if (!edges.isEmpty()) {
                sb.append("  -> ");
                List<Integer> targets = new ArrayList<>();
                for (Graph.Edge edge : edges) {
                    targets.add(edge.getDestination());
                }
                sb.append(targets).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Check if condensation is a valid DAG.
     * @return true if no cycles exist
     */
    public boolean isDAG() {
        int n = condensation.getNumVertices();
        int[] inDegree = new int[n];

        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : condensation.getEdgesFrom(u)) {
                inDegree[edge.getDestination()]++;
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int v = 0; v < n; v++) {
            if (inDegree[v] == 0) {
                queue.offer(v);
            }
        }

        int processed = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            processed++;

            for (Graph.Edge edge : condensation.getEdgesFrom(u)) {
                int v = edge.getDestination();
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }

        return processed == n;
    }
}
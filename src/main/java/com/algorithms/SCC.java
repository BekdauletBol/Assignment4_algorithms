package com.algorithms;

import com.model.Graph;
import com.utils.Metrics;

import java.util.*;

/**
 * Tarjan’s algorithm implementation for identifying
 * Strongly Connected Components (SCCs) in a directed graph.
 *
 * This algorithm performs a single DFS traversal and uses
 * discovery times with low-link values to determine SCC roots.
 *
 * Time complexity: O(V + E)
 * Space complexity: O(V)
 */
public class SCC {
    private final Graph graph;
    private final Metrics metrics;

    private int timer;
    private int[] discovery;
    private int[] lowLink;
    private boolean[] inStack;
    private Stack<Integer> stack;
    private List<List<Integer>> components;

    /**
     * Constructs a TarjanSCC instance for the specified graph.
     *
     * @param graph Directed graph for SCC analysis
     */
    public SCC(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    /**
     * Finds all strongly connected components in the graph.*/
    public SCCResult computeSCCs() {
        int numVertices = graph.getNumVertices();
        discovery = new int[numVertices];
        lowLink = new int[numVertices];
        inStack = new boolean[numVertices];
        stack = new Stack<>();
        components = new ArrayList<>();

        Arrays.fill(discovery, -1);
        Arrays.fill(lowLink, -1);

        timer = 0;
        metrics.reset();
        metrics.startTiming();

        for (int node = 0; node < numVertices; node++) {
            if (discovery[node] == -1) {
                dfs(node);
            }
        }

        metrics.stopTiming();
        return new SCCResult(components, metrics);
    }

    /**
     * Recursive DFS used by Tarjan’s algorithm to detect SCCs.
     *
     * @param u current vertex
     */
    private void dfs(int u) {
        discovery[u] = lowLink[u] = timer++;
        stack.push(u);
        inStack[u] = true;
        metrics.incrementOperations();

        for (Graph.Edge edge : graph.getEdgesFrom(u)) {
            int v = edge.getDestination();
            metrics.incrementOperations();

            if (discovery[v] == -1) {
                dfs(v);
                lowLink[u] = Math.min(lowLink[u], lowLink[v]);
            } else if (inStack[v]) {
                lowLink[u] = Math.min(lowLink[u], discovery[v]);
            }
        }

        // If u is the root of an SCC
        if (lowLink[u] == discovery[u]) {
            List<Integer> scc = new ArrayList<>();
            int vertex;
            do {
                vertex = stack.pop();
                inStack[vertex] = false;
                scc.add(vertex);
                metrics.incrementOperations();
            } while (vertex != u);

            Collections.sort(scc);
            components.add(scc);
        }
    }

    /**
     * Builds a condensation DAG where each SCC is represented as a single node.
     *
     * @param result SCC detection result*/
    public Graph createCondensedGraph(SCCResult result) {
        List<List<Integer>> sccList = result.getComponents();
        int totalSCCs = sccList.size();

        int[] vertexGroup = new int[graph.getNumVertices()];
        for (int i = 0; i < totalSCCs; i++) {
            for (int v : sccList.get(i)) {
                vertexGroup[v] = i;
            }
        }

        Graph condensed = new Graph(totalSCCs, true);
        Set<String> edgeSet = new HashSet<>();

        for (int u = 0; u < graph.getNumVertices(); u++) {
            int sccU = vertexGroup[u];
            for (Graph.Edge edge : graph.getEdgesFrom(u)) {
                int sccV = vertexGroup[edge.getDestination()];
                if (sccU != sccV) {
                    String key = sccU + "->" + sccV;
                    if (!edgeSet.contains(key)) {
                        condensed.addEdge(sccU, sccV, edge.getWeight());
                        edgeSet.add(key);
                    }
                }
            }
        }

        return condensed;
    }

    /**
     * Holds the result of the SCC computation and performance metrics.
     */
    public static class SCCResult {
        private final List<List<Integer>> components;
        private final Metrics metrics;

        public SCCResult(List<List<Integer>> components, Metrics metrics) {
            this.components = components;
            this.metrics = metrics;
        }

        public List<List<Integer>> getComponents() {
            return components;
        }

        public int[] getSizes() {
            int[] sizes = new int[components.size()];
            for (int i = 0; i < components.size(); i++) {
                sizes[i] = components.get(i).size();
            }
            return sizes;
        }

        public Metrics getMetrics() {
            return metrics;
        }
    }
}
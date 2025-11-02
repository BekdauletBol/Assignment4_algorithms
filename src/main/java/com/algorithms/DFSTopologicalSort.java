package com.algorithms;

import com.model.Graph;
import com.utils.Metrics;

import java.util.*;

/**
 * Topological sorting implementation using Depth-First Search (DFS).
 * Produces a valid order for Directed Acyclic Graphs (DAGs).
 */
public class DFSTopologicalSort {
    private final Graph inputGraph;
    private final Metrics metrics;

    /**
     * Initializes a DFS-based topological sorter.
     *
     * @param inputGraph the graph to process
     */
    public DFSTopologicalSort(Graph inputGraph) {
        this.inputGraph = inputGraph;
        this.metrics = new Metrics();
    }

    /**
     * Executes topological sorting using recursive DFS.
     *
     */
    public KahnTopologicalSort.TopoResult performSort() {
        int numVertices = inputGraph.getNumVertices();
        boolean[] visited = new boolean[numVertices];
        Stack<Integer> orderStack = new Stack<>();

        metrics.reset();
        metrics.startTiming();

        // Explore each unvisited vertex
        for (int vertex = 0; vertex < numVertices; vertex++) {
            if (!visited[vertex]) {
                explore(vertex, visited, orderStack);
            }
        }

        metrics.stopTiming();

        // Collect sorted order
        List<Integer> topoOrder = new ArrayList<>();
        while (!orderStack.isEmpty()) {
            topoOrder.add(orderStack.pop());
        }

        boolean isAcyclic = (topoOrder.size() == numVertices);

        return new KahnTopologicalSort.TopoResult(topoOrder, isAcyclic, metrics);
    }

    /**
     * Recursive DFS helper for topological sorting.
     *
     * @param current  current vertex being explored
     * @param visited  tracks visited nodes
     * @param orderStack  stores nodes in postorder
     */
    private void explore(int current, boolean[] visited, Stack<Integer> orderStack) {
        visited[current] = true;
        metrics.incrementOperations();

        for (Graph.Edge edge : inputGraph.getEdgesFrom(current)) {
            int neighbor = edge.getDestination();
            metrics.incrementOperations();

            if (!visited[neighbor]) {
                explore(neighbor, visited, orderStack);
            }
        }

        orderStack.push(current);
    }
}

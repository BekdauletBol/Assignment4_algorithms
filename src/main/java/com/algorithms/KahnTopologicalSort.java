package com.algorithms;

import com.model.Graph;
import com.utils.Metrics;

import java.util.*;

// Implementation of Kahn’s algorithm for topological sorting (BFS + in-degree)
public class KahnTopologicalSort {
    private final Graph graph;
    private final Metrics metricsTracker;

    public KahnTopologicalSort(Graph graph) {
        this.graph = graph;
        this.metricsTracker = new Metrics();
    }

    // Perform topological sorting using Kahn’s Algorithm
    public TopoResult computeTopoOrder() {
        int numVertices = graph.getNumVertices();
        int[] incomingEdges = new int[numVertices];

        metricsTracker.reset();
        metricsTracker.startTiming();

        // Calculate in-degrees for all vertices
        for (int source = 0; source < numVertices; source++) {
            for (Graph.Edge edge : graph.getEdgesFrom(source)) {
                incomingEdges[edge.getDestination()]++;
                metricsTracker.incrementOperations();
            }
        }

        Queue<Integer> zeroInDegreeQueue = new LinkedList<>();
        for (int vertex = 0; vertex < numVertices; vertex++) {
            if (incomingEdges[vertex] == 0) {
                zeroInDegreeQueue.offer(vertex);
                metricsTracker.incrementOperations();
            }
        }

        List<Integer> topoOrder = new ArrayList<>();

        // Process vertices with zero in-degree
        while (!zeroInDegreeQueue.isEmpty()) {
            int current = zeroInDegreeQueue.poll();
            topoOrder.add(current);
            metricsTracker.incrementOperations();

            for (Graph.Edge edge : graph.getEdgesFrom(current)) {
                int neighbor = edge.getDestination();
                incomingEdges[neighbor]--;
                metricsTracker.incrementOperations();

                if (incomingEdges[neighbor] == 0) {
                    zeroInDegreeQueue.offer(neighbor);
                }
            }
        }

        metricsTracker.stopTiming();

        boolean isAcyclic = (topoOrder.size() == numVertices);

        return new TopoResult(topoOrder, isAcyclic, metricsTracker);
    }

    // Result container for topological sorting
    public static class TopoResult {
        private final List<Integer> sortedOrder;
        private final boolean acyclicGraph;
        private final Metrics metricsTracker;

        public TopoResult(List<Integer> sortedOrder, boolean acyclicGraph, Metrics metricsTracker) {
            this.sortedOrder = sortedOrder;
            this.acyclicGraph = acyclicGraph;
            this.metricsTracker = metricsTracker;
        }

        public List<Integer> getOrder() {
            return sortedOrder;
        }

        public boolean isDAG() {
            return acyclicGraph;
        }

        public Metrics getMetrics() {
            return metricsTracker;
        }
    }
}

package com.algorithms;


import com.model.Graph;
import com.utils.Metrics;

import java.util.*;

/**
 * Provides algorithms for computing shortest and longest paths in Directed Acyclic Graphs (DAGs).
 */
public class DAG {
    private final Graph graph;
    private final Metrics performance;

    public DAG(Graph graph) {
        this.graph = graph;
        this.performance = new Metrics();
    }

    /**
     * Computes the shortest paths from a source vertex using topological ordering.
     *
     * @param source the source vertex
     * @return path results including distances, parents, and performance metrics
     */
    public PathResult computeShortestPaths(int source) {
        int totalVertices = graph.getNumVertices();
        int[] distance = new int[totalVertices];
        int[] previous = new int[totalVertices];
        Arrays.fill(distance, Integer.MAX_VALUE);
        Arrays.fill(previous, -1);
        distance[source] = 0;

        performance.reset();
        performance.startTiming();

        List<Integer> topoOrder = getTopologicalOrder();

        for (int u : topoOrder) {
            if (distance[u] == Integer.MAX_VALUE) continue;

            for (Graph.Edge edge : graph.getEdgesFrom(u)) {
                int v = edge.getDestination();
                int w = edge.getWeight();
                performance.incrementOperations();

                if (distance[u] + w < distance[v]) {
                    distance[v] = distance[u] + w;
                    previous[v] = u;
                }
            }
        }

        performance.stopTiming();
        return new PathResult(distance, previous, performance, false);
    }

    /**
     * Computes the longest paths from a given source vertex.
     * Essentially the same as shortest paths but uses inverted comparison.
     */
    public PathResult computeLongestPaths(int source) {
        int totalVertices = graph.getNumVertices();
        int[] distance = new int[totalVertices];
        int[] previous = new int[totalVertices];
        Arrays.fill(distance, Integer.MIN_VALUE);
        Arrays.fill(previous, -1);
        distance[source] = 0;

        performance.reset();
        performance.startTiming();

        List<Integer> topoOrder = getTopologicalOrder();

        for (int u : topoOrder) {
            if (distance[u] == Integer.MIN_VALUE) continue;

            for (Graph.Edge edge : graph.getEdgesFrom(u)) {
                int v = edge.getDestination();
                int w = edge.getWeight();
                performance.incrementOperations();

                if (distance[u] + w > distance[v]) {
                    distance[v] = distance[u] + w;
                    previous[v] = u;
                }
            }
        }

        performance.stopTiming();
        return new PathResult(distance, previous, performance, true);
    }

    /**
     * Finds the critical path (the longest path in the entire DAG).
     *
     * @return critical path details
     */
    public CriticalPathResult findCriticalPath() {
        int n = graph.getNumVertices();
        int bestSrc = -1;
        int bestDst = -1;
        int longestLength = Integer.MIN_VALUE;
        PathResult bestPathResult = null;

        for (int src = 0; src < n; src++) {
            PathResult result = computeLongestPaths(src);
            int[] dist = result.getDistances();

            for (int dst = 0; dst < n; dst++) {
                if (dist[dst] != Integer.MIN_VALUE && dist[dst] > longestLength) {
                    longestLength = dist[dst];
                    bestSrc = src;
                    bestDst = dst;
                    bestPathResult = result;
                }
            }
        }

        List<Integer> criticalPath = new ArrayList<>();
        if (bestPathResult != null && bestDst != -1) {
            criticalPath = bestPathResult.reconstructPath(bestDst);
        }

        return new CriticalPathResult(criticalPath, longestLength, bestSrc, bestDst);
    }

    /**
     * Performs a topological sort using DFS to determine vertex order.
     */
    private List<Integer> getTopologicalOrder() {
        int n = graph.getNumVertices();
        boolean[] visited = new boolean[n];
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsTopological(i, visited, stack);
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!stack.isEmpty()) {
            order.add(stack.pop());
        }
        return order;
    }

    /**
     * Depth-first search helper method for topological sorting.
     */
    private void dfsTopological(int current, boolean[] visited, Deque<Integer> stack) {
        visited[current] = true;

        for (Graph.Edge edge : graph.getEdgesFrom(current)) {
            if (!visited[edge.getDestination()]) {
                dfsTopological(edge.getDestination(), visited, stack);
            }
        }

        stack.push(current);
    }

    // ====== Inner Classes ======

    /**
     * Holds the result of a shortest or longest path computation.
     */
    public static class PathResult {
        private final int[] distances;
        private final int[] parents;
        private final Metrics metrics;
        private final boolean longest;

        public PathResult(int[] distances, int[] parents, Metrics metrics, boolean longest) {
            this.distances = distances;
            this.parents = parents;
            this.metrics = metrics;
            this.longest = longest;
        }

        public int[] getDistances() {
            return distances;
        }

        public int[] getParents() {
            return parents;
        }

        public Metrics getMetrics() {
            return metrics;
        }

        public boolean isLongest() {
            return longest;
        }

        /**
         * Reconstructs a path from the source to the given destination vertex.
         */
        public List<Integer> reconstructPath(int destination) {
            List<Integer> path = new ArrayList<>();
            int current = destination;

            while (current != -1) {
                path.add(current);
                current = parents[current];
            }

            Collections.reverse(path);
            return path;
        }
    }

    /**
     * Represents the final result of a critical path computation.
     */
    public static class CriticalPathResult {
        private final List<Integer> path;
        private final int totalLength;
        private final int start;
        private final int end;

        public CriticalPathResult(List<Integer> path, int totalLength, int start, int end) {
            this.path = path;
            this.totalLength = totalLength;
            this.start = start;
            this.end = end;
        }

        public List<Integer> getPath() {
            return path;
        }

        public int getTotalLength() {
            return totalLength;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }
}
package com.algorithms;

import com.model.Graph;
import com.utils.Metrics;

import java.util.*;

/**
 * Performs topological sorting on a condensation graph (DAG of SCCs).
 * Uses Kahn's algorithm to produce both component and task orderings.
 */
public class ComponentTS {
    private final CondensationGraph condensedGraph;
    private final Metrics metrics;

    /**
     * Initializes a topological sorter for the given condensation graph.
     *
     * @param condensedGraph condensation graph to be processed
     */
    public ComponentTS(CondensationGraph condensedGraph) {
        this.condensedGraph = condensedGraph;
        this.metrics = new Metrics();
    }

    /**
     * Executes Kahn’s algorithm to obtain the topological order of components.
     *
     * @return a {@link ComponentTopoResult} object containing both
     *         component and vertex-level orderings
     */
    public ComponentTopoResult performSort() {
        Graph graph = condensedGraph.getCondensationGraph();
        int numVertices = graph.getNumVertices();
        int[] incomingCount = new int[numVertices];

        metrics.reset();
        metrics.startTiming();

        // Step 1: Compute in-degrees for each node
        for (int u = 0; u < numVertices; u++) {
            for (Graph.Edge edge : graph.getEdgesFrom(u)) {
                incomingCount[edge.getDestination()]++;
                metrics.incrementOperations();
            }
        }

        // Step 2: Enqueue all vertices with zero in-degree
        Queue<Integer> zeroInQueue = new LinkedList<>();
        for (int v = 0; v < numVertices; v++) {
            if (incomingCount[v] == 0) {
                zeroInQueue.offer(v);
                metrics.incrementOperations();
            }
        }

        List<Integer> componentSequence = new ArrayList<>();

        // Step 3: Process nodes in topological order
        while (!zeroInQueue.isEmpty()) {
            int current = zeroInQueue.poll();
            componentSequence.add(current);
            metrics.incrementOperations();

            for (Graph.Edge edge : graph.getEdgesFrom(current)) {
                int neighbor = edge.getDestination();
                incomingCount[neighbor]--;
                metrics.incrementOperations();

                if (incomingCount[neighbor] == 0) {
                    zeroInQueue.offer(neighbor);
                }
            }
        }

        // Step 4: Derive vertex-level (task) order
        List<Integer> taskSequence = new ArrayList<>();
        for (int compId : componentSequence) {
            List<Integer> nodes = condensedGraph.getVerticesInComponent(compId);
            taskSequence.addAll(nodes);
        }

        metrics.stopTiming();

        boolean isValidDAG = (componentSequence.size() == numVertices);
        return new ComponentTopoResult(componentSequence, taskSequence, isValidDAG, metrics);
    }

    /**
     * Holds the result of the topological sort — both at the component
     * and task level — along with timing and operation metrics.
     */
    public static class ComponentTopoResult {
        private final List<Integer> componentSequence;
        private final List<Integer> taskSequence;
        private final boolean dagValid;
        private final Metrics metrics;

        /**
         * Constructs a result container for topological sorting.
         *
         * @param componentSequence topological order of SCC components
         * @param taskSequence topological order of individual vertices
         * @param dagValid indicates whether the graph was a valid DAG
         * @param metrics performance statistics
         */
        public ComponentTopoResult(List<Integer> componentSequence, List<Integer> taskSequence, boolean dagValid, Metrics metrics) {
            this.componentSequence = componentSequence;
            this.taskSequence = taskSequence;
            this.dagValid = dagValid;
            this.metrics = metrics;
        }

        public List<Integer> getComponentOrder() {
            return componentSequence;
        }

        public List<Integer> getTaskOrder() {
            return taskSequence;
        }

        public boolean isDAG() {
            return dagValid;
        }

        public Metrics getMetrics() {
            return metrics;
        }
    }
}

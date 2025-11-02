package com;

import com.algorithms.ComponentTS;
import com.algorithms.CondensationGraph;
import com.algorithms.DAG;
import com.algorithms.SCC;
import com.model.Graph;
import com.utils.GraphLoader;

import java.io.IOException;
import java.util.*;

/**
 * Reworked Main class.
 * Same processing pipeline as before (SCC -> Condensation -> Topo -> DAG paths),
 * but with cleaner printing, no emojis or special arrows, and using the
 * condensation component that contains the original source vertex for path analysis.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("========== SMART CITY GRAPH ANALYSIS ==========\n");

        String[] datasets = {
                "input/small_cyclic.json",
                "input/small_dag.json",
                "input/small_multi_scc.json",
                "input/medium_cyclic.json",
                "input/medium_dag.json",
                "input/medium_dense_scc.json",
                "input/large_cyclic_sparse.json",
                "input/large_dag_dense.json",
                "input/large_multi_scc.json"
        };

        for (String ds : datasets) {
            try {
                processFile(ds);
            } catch (IOException ex) {
                System.err.println("[ERROR] Unable to process " + ds + " : " + ex.getMessage());
            }
        }

        System.out.println("\nAll datasets have been analyzed successfully.");
    }

    private static void processFile(String filePath) throws IOException {
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println(" Dataset: " + filePath);
        System.out.println("--------------------------------------------------");

        GraphLoader.GraphData data = GraphLoader.loadFromFile(filePath);
        Graph graph = data.graph;
        int originalSource = data.sourceVertex;

        System.out.printf(Locale.US, "Vertices: %-5d | Directed: %-5b | Source: %-5d%n",
                graph.getNumVertices(), graph.isDirected(), originalSource);
        System.out.println("Weight model: " + data.weightType + " (edge weights = task duration in hours)");
        System.out.println();

        // 1) Tarjan SCC
        System.out.println("Step 1: Strongly Connected Components (Tarjan)");
        SCC sccSolver = new SCC(graph);
        SCC.SCCResult sccResult = sccSolver.computeSCCs();

        List<List<Integer>> components = sccResult.getComponents();
        System.out.println("Total SCCs: " + components.size());
        for (int i = 0; i < components.size(); i++) {
            List<Integer> comp = components.get(i);
            System.out.println("  Component [" + i + "] -> " + comp + " (size: " + comp.size() + ")");
        }
        System.out.printf(Locale.US, "Operations: %d | Time: %.3f ms%n",
                sccResult.getMetrics().getOperations(),
                sccResult.getMetrics().getElapsedMillis());
        System.out.println();

        // 2) Build condensation graph
        System.out.println("Step 2: Condensation Graph (SCC -> node)");
        CondensationGraph condensation = new CondensationGraph(graph, components);
        Graph condensed = condensation.getCondensationGraph();
        System.out.println("Condensed vertices (SCC count): " + condensed.getNumVertices());
        System.out.println("Edges between components:");
        for (int i = 0; i < condensed.getNumVertices(); i++) {
            List<Graph.Edge> edges = condensed.getEdgesFrom(i);
            if (!edges.isEmpty()) {
                List<Integer> targets = new ArrayList<>();
                for (Graph.Edge e : edges) targets.add(e.getDestination());
                System.out.println("  SCC[" + i + "] -> " + targets);
            }
        }
        System.out.println("Is condensation a DAG? " + condensation.isDAG());
        System.out.println();

        // Map original vertices to condensation component id
        int[] vertexToComp = new int[graph.getNumVertices()];
        for (int ci = 0; ci < components.size(); ci++) {
            for (int v : components.get(ci)) {
                vertexToComp[v] = ci;
            }
        }
        int condensedSource = vertexToComp[originalSource];
        System.out.println("Using condensed source component: " + condensedSource +
                " (contains original vertex " + originalSource + ")");
        System.out.println();

        // 3) Topological sort on condensation graph
        System.out.println("Step 3: Topological Sort (components)");
        ComponentTS componentSorter = new ComponentTS(condensation);
        ComponentTS.ComponentTopoResult topoRes = componentSorter.performSort();

        System.out.println("Is DAG: " + topoRes.isDAG());
        System.out.println("Component order: " + topoRes.getComponentOrder());
        System.out.println("Derived task order: " + topoRes.getTaskOrder());
        System.out.printf(Locale.US, "Operations: %d | Time: %.3f ms%n",
                topoRes.getMetrics().getOperations(),
                topoRes.getMetrics().getElapsedMillis());
        System.out.println();

        // 4) If DAG, run shortest/longest path analysis using the condensedSource
        if (topoRes.isDAG()) {
            System.out.println("Step 4: Path analysis on DAG (from condensed source " + condensedSource + ")");
            DAG dag = new DAG(condensed);

            // Shortest paths
            DAG.PathResult shortest = dag.computeShortestPaths(condensedSource);
            System.out.println();
            System.out.println("Shortest distances from component " + condensedSource + ":");
            System.out.println("--------------------------------------------------");
            System.out.printf(Locale.US, "%-12s %-12s %-20s%n", "Destination", "Distance", "Path");
            System.out.println("--------------------------------------------------");
            int[] dist = shortest.getDistances();
            for (int i = 0; i < dist.length; i++) {
                if (dist[i] == Integer.MAX_VALUE) {
                    System.out.printf(Locale.US, "%-12d %-12s %-20s%n", i, "INF", "[]");
                } else {
                    List<Integer> route = shortest.reconstructPath(i); // renamed local variable
                    System.out.printf(Locale.US, "%-12d %-12d %-20s%n", i, dist[i], route);
                }
            }
            System.out.printf(Locale.US, "Relaxations: %d | Time: %.3f ms%n",
                    shortest.getMetrics().getOperations(),
                    shortest.getMetrics().getElapsedMillis());

            // Longest paths
            System.out.println();
            System.out.println("Longest distances from component " + condensedSource + ":");
            DAG.PathResult longest = dag.computeLongestPaths(condensedSource);
            int[] longDist = longest.getDistances();
            for (int i = 0; i < longDist.length; i++) {
                if (longDist[i] == Integer.MIN_VALUE) {
                    System.out.println("  To " + i + ": -INF (unreachable)");
                } else {
                    List<Integer> route = longest.reconstructPath(i); // renamed local variable
                    System.out.println("  To " + i + ": " + longDist[i] + " | Path " + route);
                }
            }
            System.out.printf(Locale.US, "Relaxations: %d | Time: %.3f ms%n",
                    longest.getMetrics().getOperations(),
                    longest.getMetrics().getElapsedMillis());
            System.out.println();

            // 5) Critical path
            System.out.println("Step 5: Critical Path");
            DAG.CriticalPathResult critical = dag.findCriticalPath();
            System.out.println("Critical path: " + critical.getPath());
            System.out.println("Length: " + critical.getTotalLength());
            System.out.println("From component " + critical.getStart() + " to " + critical.getEnd());
        } else {
            System.out.println("Skipping path analysis: condensation graph is not a DAG.");
        }

        System.out.println();
        System.out.println("Dataset processed: " + filePath);
        System.out.println("--------------------------------------------------");
    }
}
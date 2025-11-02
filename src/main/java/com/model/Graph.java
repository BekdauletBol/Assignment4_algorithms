package com.model;
import java.util.*;

/**
 * A class representing a weighted graph using adjacency lists.
 * It can be directed or undirected and supports edge-related operations.
 * Space Complexity: O(V)
 * Edge addition: O(1)
 */
public class Graph {
    private final int numVertices;
    private final List<List<Edge>> connections;
    private final boolean isDirected;
    private String weightType;

    /**
     * Constructs a graph with the given number of vertices.
     *
     * @param numVertices total number of vertices
     * @param isDirected whether the graph is directed
     */
    public Graph(int numVertices, boolean isDirected) {
        this.numVertices = numVertices;
        this.isDirected = isDirected;
        this.connections = new ArrayList<>(numVertices);
        for (int i = 0; i < numVertices; i++) {
            connections.add(new ArrayList<>());
        }
        this.weightType = "edge";
    }


    public void addEdge(int from, int to, int weight) {
        connections.get(from).add(new Edge(to, weight));
        if (!isDirected) {
            connections.get(to).add(new Edge(from, weight));
        }
    }

    /**
     * Returns the total number of vertices.
     *
     * @return vertex count
     */
    public int getNumVertices() {
        return numVertices;
    }

    /**
     * Returns all outgoing edges from the given vertex.
     *
     * @param vertex source vertex
     * @return list of outgoing edges
     */
    public List<Edge> getEdgesFrom(int vertex) {
        return connections.get(vertex);
    }

    /**
     * Checks whether the graph is directed.
     *
     * @return true if directed, false otherwise
     */
    public boolean isDirected() {
        return isDirected;
    }

    /**
     * Returns the current weight type of this graph.
     *
     * @return weight type string
     */
    public String getWeightType() {
        return weightType;
    }

    /**
     * Updates the graph’s weight model.
     *
     * @param weightType description of the weight model
     */
    public void setWeightType(String weightType) {
        this.weightType = weightType;
    }

    /**
     * Builds and returns a reversed version of this graph (used in algorithms like Kosaraju’s).
     *
     * @return reversed graph
     * @complexity O(V + E)
     */
    public Graph reverseGraph() {
        Graph reversed = new Graph(numVertices, isDirected);
        reversed.setWeightType(this.weightType);
        for (int from = 0; from < numVertices; from++) {
            for (Edge e : connections.get(from)) {
                reversed.addEdge(e.getDestination(), from, e.getWeight());
            }
        }
        return reversed;
    }

    /**
     * Represents a weighted edge in the graph.
     */
    public static class Edge {
        private final int destination;
        private final int weight;

        /**
         * Constructs a new edge.
         *
         * @param destination destination vertex
         * @param weight edge weight
         */
        public Edge(int destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }

        public int getDestination() {
            return destination;
        }

        public int getWeight() {
            return weight;
        }
    }
}
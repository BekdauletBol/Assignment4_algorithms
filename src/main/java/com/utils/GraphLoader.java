package com.utils;

import com.model.Graph;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.FileReader;
import java.io.IOException;


public class GraphLoader {

    /**
     * Reads a JSON
     **/
    public static GraphData loadFromFile(String filePath) throws IOException {
        Gson gson = new Gson();
        JsonObject jsonRoot = gson.fromJson(new FileReader(filePath), JsonObject.class);

        boolean isDirected = jsonRoot.get("directed").getAsBoolean();
        int numVertices = jsonRoot.get("n").getAsInt();

        Graph graph = new Graph(numVertices, isDirected);
        String weightType = jsonRoot.has("weight_model")
                ? jsonRoot.get("weight_model").getAsString()
                : "edge";
        graph.setWeightType(weightType);

        JsonArray edgeList = jsonRoot.getAsJsonArray("edges");
        for (JsonElement item : edgeList) {
            JsonObject edgeObj = item.getAsJsonObject();
            int from = edgeObj.get("u").getAsInt();
            int to = edgeObj.get("v").getAsInt();
            int weight = edgeObj.get("w").getAsInt();
            graph.addEdge(from, to, weight);
        }

        int sourceVertex = jsonRoot.has("source") ? jsonRoot.get("source").getAsInt() : 0;

        return new GraphData(graph, sourceVertex, weightType);
    }

    /**
     * Container class to store loaded graph data.
     */
    public static class GraphData {
        public final Graph graph;
        public final int sourceVertex;
        public final String weightType;

        public GraphData(Graph graph, int sourceVertex, String weightType) {
            this.graph = graph;
            this.sourceVertex = sourceVertex;
            this.weightType = weightType;
        }
    }
}

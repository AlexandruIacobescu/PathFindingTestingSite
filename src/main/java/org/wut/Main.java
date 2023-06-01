package org.wut;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.wut.Utility.*;

/**
 * The Main class is the entry point of the program and provides an example usage of the Utility and Dijkstra classes.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        FileInputStream verticesFileInputStream = new FileInputStream("data/NYC/NYC-vertices.txt");
        FileInputStream weightedEdgesFileInputStream = new FileInputStream("data/NYC/USA-road-d.NY.gr.txt");

        ArrayList<String> vertices = new ArrayList<>();
        ArrayList<String[]> stringWeightedEdges = new ArrayList<>();

        fetchVerticesFromFile(verticesFileInputStream, vertices);
        fetchWeightedEdgesFromFile(weightedEdgesFileInputStream, stringWeightedEdges);

        DefaultDirectedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        addVerticesToGraph(graph, vertices);
        addEdgesToGraph(graph, stringWeightedEdges);

        EuclideanDistanceAdmissibleHeuristic<String, DefaultWeightedEdge> euclideanHeuristic = new EuclideanDistanceAdmissibleHeuristic<>(graph, new FileInputStream("data/NYC/USA-road-d.NY.co.txt"));

        BellmanFord.doBellmanFordShortestPath(graph, "1", "120");
    }
}
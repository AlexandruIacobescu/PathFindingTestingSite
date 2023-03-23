package org.wut;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.wut.Utility.*;

/**
 * The Main class is the entry point of the program and provides an example usage of the Utility and Dijkstra classes.
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        FileInputStream verticesFileInputStream = new FileInputStream("data/vertices.txt");
        FileInputStream weightedEdgesFileInputStream = new FileInputStream("data/weighted_edges.txt");

        ArrayList<String> vertices = new ArrayList<>();
        ArrayList<String[]> stringWeightedEdges = new ArrayList<>();

        fetchVerticesFromFile(verticesFileInputStream, vertices);
        fetchWeightedEdgesFromFile(weightedEdgesFileInputStream, stringWeightedEdges);

        DefaultDirectedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        addVerticesToGraph(graph, vertices);
        addEdgesToGraph(graph, stringWeightedEdges);

        Time.start();

        Dijkstra.doDijkstraShortestPath(graph, "12", "38");

        System.out.println(Time.stop() + " millis");
    }
}
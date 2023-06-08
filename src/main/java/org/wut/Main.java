package org.wut;

import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static org.wut.Utility.*;
import static org.wut.splicing.GraphSplitter.createGraphFromKSPlit;

/**
 * The Main class is the entry point of the program and provides an example usage of the Utility and Dijkstra classes.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        FileInputStream verticesFileInputStream = new FileInputStream("data/NYC/NYC-vertices.txt");
        FileInputStream weightedEdgesFileInputStream = new FileInputStream("data/NYC/USA-road-d.NY.gr.txt");

        ArrayList<String> vertices = new ArrayList<>();
        ArrayList<String[]> stringWeightedEdges = new ArrayList<>();

        DefaultDirectedGraph<String, DefaultWeightedEdge> graph = createGraphFromKSPlit("data/NYC/USA-road-d.NY.co.txt", "data/NYC/USA-road-d.NY.gr.txt", 9);

        FloydWarshallShortestPaths<String, DefaultWeightedEdge> floydWarshall = new FloydWarshallShortestPaths<>(graph);

        Time.start();
        FloydWarshall.doFloydWarshallShortestPathReusable(graph, floydWarshall, "56846", "79013");
        System.out.println("Floyd-Warshall ran in " + Time.stop() + " milliseconds on approx. 1/81 of the graph\n");

        Time.start();
        Johnson.doJohnsonShortestPaths(graph, "56846", "79013");
        System.out.println("Johnson ran in " + Time.stop() + " milliseconds on approx. 1/81 of the graph\n");

        fetchVerticesFromFile(verticesFileInputStream, vertices);
        fetchWeightedEdgesFromFile(weightedEdgesFileInputStream, stringWeightedEdges);

        graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        addVerticesToGraph(graph, vertices);
        addEdgesToGraph(graph, stringWeightedEdges);

        Time.start();
        Dijkstra.doDijkstraShortestPath(graph, "2", "259950");
        System.out.println("Dijkstra ran in " + Time.stop() + " milliseconds on the whole graph\n");

        EuclideanDistanceAdmissibleHeuristic<String,DefaultWeightedEdge> euclideanDistanceAdmissibleHeuristic = new EuclideanDistanceAdmissibleHeuristic<>(graph, new FileInputStream("data/NYC/USA-road-d.NY.co.txt"));
        try{
            Time.start();
            AStar.doAStarShortestPath(graph, "2", "259950", euclideanDistanceAdmissibleHeuristic);
            System.out.println("A* ran in  " + Time.stop() + " milliseconds on the whole graph\n");
        }catch(Exception ex) {
            System.out.println(ex.toString());
        }

        Time.start();
        BellmanFord.doBellmanFordShortestPath(graph, "1", "264346");
        System.out.println("Bellman-Ford ran in  " + Time.stop() + " milliseconds on the whole graph\n");
    }
}
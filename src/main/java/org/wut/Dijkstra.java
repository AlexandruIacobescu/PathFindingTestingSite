package org.wut;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * The Dijkstra class provides a static method for calculating the shortest path between two vertices in a graph using Dijkstra's algorithm.
 */
public class Dijkstra {

    /**
     * Calculates the shortest path between two vertices in a graph using Dijkstra's algorithm and prints the result.
     *
     * @param graph             The graph to calculate the shortest path on.
     * @param sourceVertex      The source vertex of the shortest path.
     * @param destinationVertex The destination vertex of the shortest path.
     */
    public static void doDijkstraShortestPath(DefaultDirectedGraph<String, DefaultWeightedEdge> graph, String sourceVertex, String destinationVertex){
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(graph);
        GraphPath<String, DefaultWeightedEdge> path = dijkstra.getPath(sourceVertex, destinationVertex);
        double weight = dijkstra.getPathWeight(sourceVertex, destinationVertex);
        System.out.println("The weight of the shortest path is: " + weight);
        System.out.println("The vertex path is: " + path);
    }
}

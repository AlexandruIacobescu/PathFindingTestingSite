package org.wut;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.alg.shortestpath.JohnsonShortestPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Johnson {

    public static void doJohnsonShortestPaths(DefaultDirectedGraph<String, DefaultWeightedEdge> graph, String sourceVertex, String destinationVertex) {
        JohnsonShortestPaths<String, DefaultWeightedEdge> johnson = new JohnsonShortestPaths<>(graph);
        GraphPath<String, DefaultWeightedEdge> path = johnson.getPath(sourceVertex, destinationVertex);
        double weight = johnson.getPathWeight(sourceVertex, destinationVertex);
        System.out.println("The weight of the shortest path is: " + weight);
        System.out.println("The vertex path is: " + path);
    }
}

package org.wut;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class FloydWarshall {

    static void doFloydWarshallShortestPaths(DefaultDirectedGraph<String, DefaultWeightedEdge> graph, String sourceVertex, String destinationVertex){
        FloydWarshallShortestPaths<String, DefaultWeightedEdge> floydWarshall = new FloydWarshallShortestPaths<>(graph);
        GraphPath<String, DefaultWeightedEdge> path = floydWarshall.getPath(sourceVertex, destinationVertex);
        double weight = floydWarshall.getPathWeight(sourceVertex, destinationVertex);
        System.out.println("The weight of the shortest path is: " + weight);
        System.out.println("The vertex path is: " + path);
    }
}

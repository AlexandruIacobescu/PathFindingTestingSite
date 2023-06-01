package org.wut;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class BellmanFord {
    public static void doBellmanFordShortestPath(DefaultDirectedGraph<String, DefaultWeightedEdge> graph, String sourceVertex, String destinationVertex){
        BellmanFordShortestPath<String, DefaultWeightedEdge> bellmanFord = new BellmanFordShortestPath<>(graph);
        GraphPath<String, DefaultWeightedEdge> path = bellmanFord.getPath(sourceVertex, destinationVertex);
        double weight = bellmanFord.getPathWeight(sourceVertex, destinationVertex);
        System.out.println("The weight of the shortest path is: " + weight);
        System.out.println("The vertex path is: " + path);
    }
}

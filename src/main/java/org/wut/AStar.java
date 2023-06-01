package org.wut;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class AStar {

    public static void doAStarShortestPath(DefaultDirectedGraph<String, DefaultWeightedEdge> graph, String sourceVertex, String destinationVertex, AStarAdmissibleHeuristic<String> heuristic){
        AStarShortestPath<String, DefaultWeightedEdge> aStar = new AStarShortestPath<>(graph, heuristic);
        GraphPath<String, DefaultWeightedEdge> path = aStar.getPath(sourceVertex, destinationVertex);
        double weight = aStar.getPathWeight(sourceVertex, destinationVertex);
        System.out.println("The weight of the shortest path is: " + weight);
        System.out.println("The vertex path is: " + path);
    }
}

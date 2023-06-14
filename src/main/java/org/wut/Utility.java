package org.wut;

import org.wut.splicing.Vertex;
import org.wut.splicing.Edge;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.wut.splicing.GraphSplitter;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

/**
 * The Utility class provides static methods for adding vertices and edges to a graph and fetching them from a file.
 */
public class Utility {
    /**
     * Fetches vertices from a file and adds them to an ArrayList.
     *
     * @param fis      The FileInputStream to read vertices from.
     * @param vertices The ArrayList to add vertices to.
     */
    static void fetchVerticesFromFile(FileInputStream fis, ArrayList<String> vertices){
        Scanner scanner = new Scanner(fis);
        while(scanner.hasNextLine())
            vertices.add(scanner.nextLine());
        scanner.close();
    }

    /**
     * Adds vertices to a graph.
     *
     * @param graph    The graph to add vertices to.
     * @param vertices The ArrayList containing the vertices to add.
     */
    public static void addVerticesToGraph(Graph<String, DefaultWeightedEdge> graph, ArrayList<String> vertices){
        for(var v : vertices)
            graph.addVertex(v);
    }

    /**
     * Fetches weighted edges from a file and adds them to an ArrayList.
     *
     * @param fis               The FileInputStream to read edges from.
     * @param stringWeightedEdges The ArrayList to add edges to.
     */
    static void fetchWeightedEdgesFromFile(FileInputStream fis, ArrayList<String[]> stringWeightedEdges){
        Scanner scanner = new Scanner(fis);
        while(scanner.hasNextLine())
            stringWeightedEdges.add(scanner.nextLine().split(" "));
    }

    /**
     * Adds weighted edges to a graph.
     *
     * @param graph              The graph to add edges to.
     * @param stringWeightedEdges The ArrayList containing the edges to add.
     */
    public static void addEdgesToGraph(DefaultDirectedGraph<String, DefaultWeightedEdge> graph, ArrayList<String[]> stringWeightedEdges){
        for(var swe : stringWeightedEdges) {
                graph.setEdgeWeight(graph.addEdge(swe[0], swe[1]), Double.parseDouble(swe[2]));
        }
    }

    static void addFirstNVerticesToGraph(DefaultDirectedGraph<String,DefaultWeightedEdge> graph, ArrayList<String> vertices, int max){
        int c = 0;
        for(var v : vertices) {
            if(++c > max)
                break;
            graph.addVertex(v);
        }
    }

    static void addEdgesToGraphIfPossible(DefaultDirectedGraph<String, DefaultWeightedEdge> graph, ArrayList<String[]> stringWeightedEdges){
        for(var swe : stringWeightedEdges){
            if(graph.containsVertex(swe[0]) && graph.containsVertex(swe[1]))
                graph.setEdgeWeight(graph.addEdge(swe[0], swe[1]), Double.parseDouble(swe[2]));
        }
    }

    public static void addVerticesToGraphFromVertexList(DefaultDirectedGraph<String,DefaultWeightedEdge> graph, Collection<Vertex> vertices){
        for(var v : vertices)
            graph.addVertex(v.label);
    }

    public static void addEdgesToGraphFromEdgeList(DefaultDirectedGraph<String,DefaultWeightedEdge> graph, Collection<Edge> edges){
        for(var e : edges){
            graph.setEdgeWeight(graph.addEdge(e.sourceVertexLabel, e.targetVertexLabel), e.weight);
        }
    }
}
package org.wut.tests;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.alg.shortestpath.JohnsonShortestPaths;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.wut.Time;
import org.wut.splicing.Edge;
import org.wut.splicing.GraphSplitter;
import org.wut.splicing.Vertex;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.wut.splicing.GraphSplitter.createGraphFromKSplitTiles;

public class APSP {

    static void runJohnsonTest() throws IOException {
        ArrayList<Vertex> vertices = GraphSplitter.vertexReader("data/NYC/USA-road-d.NY.co.txt");
        ArrayList<Edge> edges = GraphSplitter.edgesReader("data/NYC/USA-road-d.NY.gr.txt");

        FileWriter writer = new FileWriter("data/test_results/johnson.csv");
        writer.write("V,E,E/V,Johnson\n");


        int k = 100;
        int increment = 100;
        int start = 100;
        int vertexCount = 0;
        for(int i = start; i <= k * k; i+=increment){
            DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = createGraphFromKSplitTiles(vertices, edges, k, i);
            JohnsonShortestPaths<String,DefaultWeightedEdge> johnson = new JohnsonShortestPaths<>(graph);



            if(start == i){
                vertexCount = graph.vertexSet().size();
            }
            else{
                if(vertexCount == graph.vertexSet().size()){
                    vertexCount = graph.vertexSet().size();
                    continue;
                }
                else{
                    vertexCount = graph.vertexSet().size();
                }
            }

            int edgeCount = graph.edgeSet().size();

            Pair<String,String> pair = getPathConnectedVertexPair(graph);

            System.out.println("i= " + i + " johnson...");
            Time.start();
            johnson.getPath(pair.getFirst(),pair.getSecond());
            writer.write(vertexCount + "," + edgeCount + "," + String.format("%.3f", (double)edgeCount/vertexCount) + "," + Time.stop() + "\n");
            writer.flush();

        }
        writer.close();
    }

    static Pair<String,String> getPathConnectedVertexPair(DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> graph){
        ConnectivityInspector<String,DefaultWeightedEdge> inspector = new ConnectivityInspector<>(graph);
        for(var v1 : graph.vertexSet()){
            for(var v2 : graph.vertexSet()){
                if(!v1.equals(v2)){
                    if(inspector.pathExists(v1,v2))
                        return new Pair<>(v1, v2);
                }
            }
        }
        return null;
    }

    static void runFloydWarshallTest() throws IOException {
        ArrayList<Vertex> vertices = GraphSplitter.vertexReader("data/NYC/USA-road-d.NY.co.txt");
        ArrayList<Edge> edges = GraphSplitter.edgesReader("data/NYC/USA-road-d.NY.gr.txt");

        FileWriter writer = new FileWriter("data/test_results/floyd_warshall_2.csv");
        writer.write("V,E,E/V,Floyd-Warshall\n");


        int k = 60;
        int increment = 2;
        int start = 1;
        int vertexCount = 0, edgeCount = 0;
        for(int i = start; i <= k * k; i+=increment){
            DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> graph = createGraphFromKSplitTiles(vertices, edges, k, i);
            FloydWarshallShortestPaths<String,DefaultWeightedEdge> floydWarshall = new FloydWarshallShortestPaths<>(graph);

            vertexCount = graph.vertexSet().size();
            edgeCount = graph.edgeSet().size();

            Pair<String,String> pair = getPathConnectedVertexPair(graph);

            System.out.println("i= " + i + " fw...");
            Time.start();
            floydWarshall.getPath(pair.getFirst(), pair.getSecond());
            writer.write(vertexCount + "," + edgeCount + "," + String.format("%.3f", (double)edgeCount/vertexCount) + "," + Time.stop() + "\n");
            writer.flush();
            i+=increment;
        }
        writer.close();

    }

    public static void main(String[] args) throws IOException {
        runJohnsonTest();
        runFloydWarshallTest();
    }
}

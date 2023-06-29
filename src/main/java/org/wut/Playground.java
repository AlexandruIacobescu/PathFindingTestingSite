package org.wut;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.alg.shortestpath.JohnsonShortestPaths;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphDelegator;
import org.jgrapht.util.SupplierUtil;
import org.jheaps.tree.FibonacciHeap;

import org.wut.splicing.Edge;
import org.wut.splicing.GraphSplitter;
import org.wut.splicing.Vertex;
import scala.collection.immutable.Nil;

import javax.sound.sampled.Line;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.wut.Main.vertexWithHighestOutDegree;
import static org.wut.Utility.*;
import static org.wut.splicing.GraphSplitter.createGraphFromKSplitTiles;
import static org.wut.splicing.GraphSplitter.findMinMaxVertexInGraph;

public class Playground {

    static void addVerticesToGraphFromFile(FileInputStream fis, Graph<Integer,DefaultWeightedEdge> graph){
        Scanner scanner = new Scanner(fis);
        int counter = 0;
        while(scanner.hasNextLine()){
            String[] bits = scanner.nextLine().split(" ");
            graph.addVertex(Integer.parseInt(bits[0]));
            //System.out.print("\rVertices: " + ++counter);
        }
        scanner.close();
    }

    static void addWeighedEdgesToGraphFromFile(FileInputStream fis, Graph<Integer,DefaultWeightedEdge> graph){
        Scanner scanner = new Scanner(fis);
        int counter = 0;
        while(scanner.hasNextLine()){
            String[] bits = scanner.nextLine().split(" ");
            graph.setEdgeWeight(graph.addEdge(Integer.parseInt(bits[0]), Integer.parseInt(bits[1])), Double.parseDouble(bits[2]));
            System.out.print("\rEdges: " + ++counter);
        }
        scanner.close();
    }



    public static void main(String[] args) throws IOException {
        ArrayList<Vertex> vertices = GraphSplitter.vertexReader("data/FLA/USA-road-d.FLA.co.txt");
        ArrayList<Edge> edges = GraphSplitter.edgesReader("data/FLA/USA-road-d.FLA.gr.txt");

        FileWriter writer = new FileWriter("data/play.csv");
        writer.write("V,E,E/V,Johnson,Floyd-Warshall\n");


        int k = 100;
        int increment = 100;

        for(int i = increment; i <= k * k; i+=increment){
            DefaultDirectedGraph<String,DefaultWeightedEdge> graph = createGraphFromKSplitTiles(vertices, edges, k, i);

            FloydWarshallShortestPaths<String,DefaultWeightedEdge> floydWarshall = new FloydWarshallShortestPaths<>(graph);
            JohnsonShortestPaths<String,DefaultWeightedEdge> johnson = new JohnsonShortestPaths<>(graph);

            int vertexCount = graph.vertexSet().size();
            int edgeCount = graph.edgeSet().size();

            String src = (String) graph.vertexSet().toArray()[0];

//            System.out.println("i= " + i + " johnson...");
//            Time.start();
//            johnson.getPaths(src);
//            writer.write(vertexCount + "," + edgeCount + "," + String.format("%.3f", (double)edgeCount/vertexCount) + "," + Time.stop() + ",");
//            writer.flush();

            System.out.println("i= " + i + " fw...");
            Time.start();
            System.out.print("\rSP: " + floydWarshall.getPaths(src).toString());
            writer.write(Time.stop() + "\n");
            writer.flush();

        }
        writer.close();
    }
}

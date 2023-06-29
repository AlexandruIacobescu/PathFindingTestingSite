package org.wut.tests;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jheaps.tree.FibonacciHeap;
import org.wut.EuclideanDistanceAdmissibleHeuristic;
import org.wut.Time;
import org.wut.splicing.Edge;
import org.wut.splicing.GraphSplitter;
import org.wut.splicing.Vertex;
import scala.Array;
import scala.util.parsing.combinator.testing.Str;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.wut.splicing.GraphSplitter.createGraphFromKSplitTiles;
import static org.wut.splicing.GraphSplitter.findMinMaxVertexInGraph;

public class SSSP {
    static void testAStar(AStarShortestPath<String, DefaultWeightedEdge> aStarSP, DefaultDirectedGraph<String,DefaultWeightedEdge> graph, String src, String sink, FileWriter writer) throws IOException {
        ConnectivityInspector<String,DefaultWeightedEdge> inspector = new ConnectivityInspector<>(graph);
        boolean brk = false;
        int c = 1;
        for(int i = Integer.parseInt(sink); i >= Integer.parseInt(src) && !brk; i--){
            if(inspector.pathExists(src, sink)){
                try {
                    Time.start();
                    aStarSP.getPathWeight(src, String.valueOf(i));
                } catch(Exception ignored){
                    Time.stop();
                    continue;
                }
                writer.write(Time.stop() + "," + aStarSP.getPath(src,String.valueOf(i)).getLength() + '\n');
                writer.flush();
                if(!sink.equals(String.valueOf(i)))
                    System.out.println("\033[31mA* fail:\033[0m original sink: " + sink + "; new sink: " + i + "; diff: " + (Integer.parseInt(sink) - i));
                brk = true;
            }
            if(i == Integer.parseInt(src)) {
                src = getNthOutDegreeVertex(graph, ++c);
                i = Integer.parseInt(sink);
            }
        }
        if(!brk) {
            writer.write("Fail\n");
            writer.flush();
        }
    }

    static String vertexWithHighestOutDegree(DefaultDirectedGraph<String,DefaultWeightedEdge> graph){
        int max = -1;
        String result = null;
        for(var v : graph.vertexSet()){
            if(max <= graph.outDegreeOf(v)) {
                max = graph.outDegreeOf(v);
                result = v;
            }
        }

        return result;
    }

    static void runDijkstraAndBellmanFordTest() throws IOException {
        ArrayList<Vertex> vertices = GraphSplitter.vertexReader("data/FLA/USA-road-d.FLA.co.txt");
        ArrayList<Edge> edges = GraphSplitter.edgesReader("data/FLA/USA-road-d.FLA.gr.txt");

        FileWriter writer = new FileWriter("data/test_results/florida_k_10_ti_10_3.csv");
        writer.write("V,E,E/V,dijkstra,bellman-ford\n");


        int k = 10;
        boolean c = false;
        String popularVertex = "";
        for(int i = 10; i <= k * k; i+=10){
            DefaultDirectedGraph<String,DefaultWeightedEdge> graph = createGraphFromKSplitTiles(vertices, edges, k, i);

            DijkstraShortestPath<String,DefaultWeightedEdge> dijkstraSP = new DijkstraShortestPath<>(graph, FibonacciHeap::new);
            BellmanFordShortestPath<String,DefaultWeightedEdge> bellmanFordSP = new BellmanFordShortestPath<>(graph);


            String[] min_max = findMinMaxVertexInGraph(graph);
            String src = min_max[0];
            String sink = min_max[1];

            if(!c) {
                popularVertex = vertexWithHighestOutDegree(graph);
                c = true;
            }

            int vertexCount = graph.vertexSet().size();
            int edgeCount = graph.edgeSet().size();

            System.out.println("Dijkstra SP [k = " + k + ", tc = " + i + "] on source vertex: `" + popularVertex + "` running...");
            Time.start();
            dijkstraSP.getPaths(popularVertex);
            writer.write(vertexCount + "," + edgeCount + "," + String.format("%.3f", (double)edgeCount/vertexCount) + "," + Time.stop() + ",");
            writer.flush();

            System.out.println("Bellman-Ford SP [k = " + k + ", tc = " + i + "] on source vertex: `" + popularVertex + "` running...");
            Time.start();
            bellmanFordSP.getPaths(popularVertex);
            writer.write(Time.stop() + "\n");
            writer.flush();

        }
        writer.close();
    }

    // A* util (primarily)

    public static String getNthOutDegreeVertex(DefaultDirectedGraph<String, DefaultWeightedEdge> graph, int n) {
        Map<String, Integer> outDegrees = graph.vertexSet().stream()
                .collect(Collectors.toMap(
                        vertex -> vertex,
                        vertex -> graph.outDegreeOf(vertex)
                ));

        return outDegrees.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .skip(n - 1)
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // A* util (primarily) - END

    static void runAStarTest() throws IOException {
        ArrayList<Vertex> vertices = GraphSplitter.vertexReader("data/FLA/USA-road-d.FLA.co.txt");
        ArrayList<Edge> edges = GraphSplitter.edgesReader("data/FLA/USA-road-d.FLA.gr.txt");
        FileWriter writer = new FileWriter("data/test_results/florida_k-10_ti-10_a_star_2.csv");
        writer.write("V,E,E/V,A-Star,depth\n");

        int k = 11;
        int increment = 11;
        boolean c = false;
        String popularVertex = "";
        String src = "";

        for(int i = increment; i <= k * k; i+=increment){
            DefaultDirectedGraph<String,DefaultWeightedEdge> graph = createGraphFromKSplitTiles(vertices, edges, k, i);
            EuclideanDistanceAdmissibleHeuristic<String,DefaultWeightedEdge> euclideanHeuristic = new EuclideanDistanceAdmissibleHeuristic<>(graph, new FileInputStream("data/FLA/USA-road-d.FLA.co.txt"));
            AStarShortestPath<String,DefaultWeightedEdge> aStarSP = new AStarShortestPath<>(graph,euclideanHeuristic);

            if (!c) {
                src = getNthOutDegreeVertex(graph, 1);
                c = true;
            }

            String sink = findMinMaxVertexInGraph(graph)[1];

            System.out.println("src: " + src  + " sink: " + sink);

            int vertexCount = graph.vertexSet().size();
            int edgeCount = graph.edgeSet().size();

            writer.write(vertexCount + "," + edgeCount + "," + String.format("%.3f", (double)edgeCount/vertexCount) + ",");
            writer.flush();

            testAStar(aStarSP, graph, src, sink, writer);
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        runAStarTest();
        runDijkstraAndBellmanFordTest();
    }
}

package org.wut.splicing;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraManyToManyShortestPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.SupplierUtil;
import org.jheaps.tree.FibonacciHeap;
import org.wut.*;

import java.io.*;
import java.util.*;

import static org.wut.Utility.*;

public class GraphSplitter {

    public static ArrayList<Vertex> vertexReader(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(filename));
        ArrayList<Vertex> vertices = new ArrayList<>();
        while(scanner.hasNextLine()){
            String[] bits = scanner.nextLine().split(" ");
            String label = bits[0];
            double x = Double.parseDouble(bits[1]);
            double y = Double.parseDouble(bits[2]);
            vertices.add(new Vertex(label, x, y));
        }
        scanner.close();
        return vertices;
    }

    public static ArrayList<Edge> edgesReader(String filename) throws FileNotFoundException{
        Scanner scanner = new Scanner(new FileInputStream(filename));
        ArrayList<Edge> edges = new ArrayList<>();
        while(scanner.hasNextLine()){
            String[] bits = scanner.nextLine().split(" ");
            String sourceVertexLabel = bits[0];
            String targetvertexLabel = bits[1];
            double weight = Double.parseDouble(bits[2]);
            edges.add(new Edge(sourceVertexLabel, targetvertexLabel, weight));
        }
        scanner.close();
        return edges;
    }

    private static ArrayList<Vertex> getVerticesKSplitBottomLeftTile(List<Vertex> vertices, double k, double xMin, double yMin, double distXminXmax, double distYminYmax){
        ArrayList<Vertex> result = new ArrayList<>();
        for(var v : vertices){
            if(v.x >= xMin && v.x <= xMin + distXminXmax/k && v.y <= yMin + distYminYmax/k && v.y >= yMin)
                result.add(v);
        }
        return result;
    }

    private static Vertex[] getLeftRightUpBottomMostVertices(List<Vertex> vertices) {
        Vertex leftMost = vertices.get(0);
        Vertex rightMost = leftMost;
        Vertex upMost = leftMost;
        Vertex bottomMost = upMost;

        for(var v : vertices) {
            if(v.x < leftMost.x)
                leftMost = v;
            if(v.x > rightMost.x)
                rightMost = v;
            if(v.y < bottomMost.y)
                bottomMost = v;
            if(v.y > upMost.y)
                upMost = v;
        }
        return new Vertex[]{leftMost, rightMost, upMost, bottomMost};
    }

    private static double getDistXminXmax(double xMin, double xMax){
        if(xMin < 0 && xMax < 0)
            return Math.abs(xMin - xMax);
        if(xMin < 0 && xMax >= 0)
            return -xMin + xMax;
        if(xMin >= 0 && xMax >= 0)
            return xMax - xMin;
        return 0;
    }

    private static double getDistYminYmax(double yMin, double yMax){
        if(yMin < 0 && yMax < 0)
            return Math.abs(yMin - yMax);
        if(yMin < 0 && yMax >= 0)
            return -yMin + yMax;
        if(yMin >= 0 && yMax >= 0)
            return yMax - yMin;
        return 0;
    }

    public static void flushKSplit(ArrayList<Vertex> vertices, double xMin, double xMax, double yMin, double yMax, double k, String filename) throws IOException{
        double xMinXmaxDist = getDistXminXmax(xMin, xMax);
        double yMinYmaxDist = getDistYminYmax(yMin, yMax);
        ArrayList<Vertex> result = getVerticesKSplitBottomLeftTile(vertices, k, xMin, yMin, xMinXmaxDist, yMinYmaxDist);

        FileWriter writer = new FileWriter(filename);

        for(var v : result)
            writer.write(v.toString() + "\n");

        writer.close();
    }

    private static ArrayList<Vertex> getKSplitVertices(ArrayList<Vertex> vertices, double xMin, double xMax, double yMin, double yMax, double k){
        double xMinXmaxDist = getDistXminXmax(xMin, xMax);
        double yMinYmaxDist = getDistYminYmax(yMin, yMax);
        return getVerticesKSplitBottomLeftTile(vertices, k, xMin, yMin, xMinXmaxDist, yMinYmaxDist);
    }

    private static ArrayList<Edge> getEdgesInKSplit(Collection<Vertex> vertices, Collection<Edge> edges) {
        HashSet<String> vertexLabels = new HashSet<>();
        for (Vertex v : vertices) {
            vertexLabels.add(v.label);
        }

        ArrayList<Edge> result = new ArrayList<>();
        for (Edge e : edges) {
            if (vertexLabels.contains(e.sourceVertexLabel) && vertexLabels.contains(e.targetVertexLabel)) {
                result.add(e);
            }
        }
        return result;
    }

    public static DefaultDirectedGraph<String, DefaultWeightedEdge> createGraphFromKSplit(String verticesCoordinatesFile, String weightedEdgesFile, double k, boolean withSupplier) throws FileNotFoundException {
        DefaultDirectedGraph<String, DefaultWeightedEdge> graph;
        if(!withSupplier)
             graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        else
            graph = new DefaultDirectedWeightedGraph<>(SupplierUtil.createStringSupplier(1), SupplierUtil.DEFAULT_WEIGHTED_EDGE_SUPPLIER);
        ArrayList<Vertex> vertices;
        ArrayList<Edge> edges;
        ArrayList<Edge> edgesInKSplit;
        ArrayList<Vertex> verticesInKSplit;
        ArrayList<String> verticesLabels = new ArrayList<>();
        ArrayList<String[]> stringWeightedEdges = new ArrayList<>();

        vertices = vertexReader(verticesCoordinatesFile);
        edges = edgesReader(weightedEdgesFile);

        Vertex[] extremes = getLeftRightUpBottomMostVertices(vertices);

        double xMin = extremes[0].x;
        double xMax = extremes[1].x;
        double yMin = extremes[3].y;
        double yMax = extremes[2].y;

        double distXminXmax = getDistXminXmax(xMin, xMax);
        double distYminYmax = getDistYminYmax(yMin, yMax);

        verticesInKSplit = getVerticesKSplitBottomLeftTile(vertices, k, xMin, yMin, distXminXmax, distYminYmax);
        edgesInKSplit = getEdgesInKSplit(verticesInKSplit, edges);

        for(var v : verticesInKSplit)
            verticesLabels.add(v.label);

        for(var e : edgesInKSplit){
            String[] swe = new String[3];
            swe[0] = e.sourceVertexLabel;
            swe[1] = e.targetVertexLabel;
            swe[2] = String.valueOf(e.weight);
            stringWeightedEdges.add(swe);
        }

        addVerticesToGraph(graph, verticesLabels);
        addEdgesToGraph(graph, stringWeightedEdges);

        return graph;
    }

    public static String[] findMinMaxVertexInGraph(Graph<String,DefaultWeightedEdge> graph) {
        HashSet<Integer> vertexSet = new HashSet<>();
        for(var v : graph.vertexSet()){
            vertexSet.add(Integer.parseInt(v));
        }

        ConnectivityInspector<String,DefaultWeightedEdge> inspector = new ConnectivityInspector<>(graph);

        int minVertex = Collections.min(vertexSet);
        int maxVertex = Collections.max(vertexSet);

        if(!inspector.pathExists(String.valueOf(minVertex), String.valueOf(maxVertex)))
            for(int i = maxVertex - 1; i >= minVertex; i--){
                if(vertexSet.contains(i))
                    if(inspector.pathExists(String.valueOf(minVertex), String.valueOf(i)))
                        return new String[]{String.valueOf(minVertex), String.valueOf(i)};
            }
        else
            return new String[]{String.valueOf(minVertex), String.valueOf(maxVertex)};
        return null;
    }

    public static void testAllInKTestRangeAndFlushToCsvFileFormat(String verticesCoordinatesFile, String weightedEdgesFile, int kmin, int kmax, String csvFileName) throws IOException {
        DefaultDirectedGraph<String,DefaultWeightedEdge> graph;
        FileWriter writer = new FileWriter(csvFileName);
        writer.write("k,dijkstra,bellman-ford,a*,johnson,floyd-warshall\n");
        for(int i = kmax; i >= kmin; i--){
            graph = createGraphFromKSplit("data/NYC/USA-road-d.NY.co.txt", "data/NYC/USA-road-d.NY.gr.txt", i, true);
            EuclideanDistanceAdmissibleHeuristic<String,DefaultWeightedEdge> euclideanDistanceAdmissibleHeuristic = new EuclideanDistanceAdmissibleHeuristic<>(graph, new FileInputStream("data/NYC/USA-road-d.NY.co.txt"));
            String[] minMaxVertex = findMinMaxVertexInGraph(graph);
            String minVertex = minMaxVertex[0];
            String maxVertex = minMaxVertex[1];

            Time.start();
            Dijkstra.doDijkstraShortestPath(graph, minVertex, maxVertex);
            writer.write(i + "," + Time.stop() + ",");
            writer.flush();

            Time.start();
            BellmanFord.doBellmanFordShortestPath(graph, minVertex, maxVertex);
            writer.write(Time.stop() + ",");
            writer.flush();

            try {
                Time.start();
                AStar.doAStarShortestPath(graph, minVertex, maxVertex, euclideanDistanceAdmissibleHeuristic);
                writer.write(Time.stop() + ",");
                writer.flush();
            }catch(Exception ignored){
                Time.stop();
                writer.write("exception" + ",");
                writer.flush();
            }

            Time.start();
            Johnson.doJohnsonShortestPaths(graph, minVertex, maxVertex);
            writer.write(Time.stop() + ",");
            writer.flush();

            Time.start();
            FloydWarshall.doFloydWarshallShortestPath(graph, minVertex, maxVertex);
            writer.write(Time.stop() + "\n");
            writer.flush();

            System.out.println("cycle " + i + " done.");
        }
        writer.close();
    }

    public static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> createGraphFromKSplitTiles(ArrayList<Vertex> vertices, ArrayList<Edge> edges, int k, int tilesCount){
        if(tilesCount < 1 || tilesCount > k * k)
            throw new IllegalArgumentException("The number of tiles have to be in the interval [1," + k * k + "]");
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(SupplierUtil.createStringSupplier(), SupplierUtil.DEFAULT_WEIGHTED_EDGE_SUPPLIER);
        Vertex[] extremes = getLeftRightUpBottomMostVertices(vertices);
        HashSet<Vertex> admissibleVertices = new HashSet<>();

        double xMin = extremes[0].x;
        double xMax = extremes[1].x;
        double yMin = extremes[3].y;
        double yMax = extremes[2].y;

        double xDist = getDistXminXmax(xMin, xMax);
        double yDist = getDistYminYmax(yMin, yMax);

        if(k < tilesCount){
            int h = tilesCount / k;
            int offset = tilesCount % k;
            if(offset != 0){
                for(var v : vertices){
                    if(v.y <= yMin + (h * yDist) / k || (v.y > yMin + (h * yDist) / k && v.y <= yMin + ((h + 1) * yDist) / k && v.x <= xMin + xMin + (offset * xDist) / k))
                        admissibleVertices.add(v);
                }
            } else {
                for(var v : vertices){
                    if(v.y <= yMin + (h * yDist) / k)
                        admissibleVertices.add(v);
                }
            }
        } else {
            for(var v : vertices) {
                if(v.y <= yMin + yDist / k && v.x <= xMin + (tilesCount * xDist) / k)
                    admissibleVertices.add(v);
            }
        }

        HashSet<Edge> admissibleEdges = new HashSet<>(getEdgesInKSplit(admissibleVertices, edges));
        addVerticesToGraphFromVertexList(graph, admissibleVertices);
        addEdgesToGraphFromEdgeList(graph, admissibleEdges);

        return graph;
    }

    public static void main(String[] args) throws Exception {
        ArrayList<Vertex> vertices = vertexReader("data/NYC/USA-road-d.NY.co.txt");
        ArrayList<Edge> edges = edgesReader("data/NYC/USA-road-d.NY.gr.txt");

//        testAllInKTestRangeAndFlushToCsvFileFormat("data/NYC/USA-road-d.NY.co.txt", "data/NYC/USA-road-d.NY.gr.txt", 2, 15, "data/test.csv");

        DefaultDirectedGraph<String,DefaultWeightedEdge> graph;
        graph = createGraphFromKSplitTiles(vertices, edges, 10, 15);

        String[] minMaxVertex = findMinMaxVertexInGraph(graph);
        String minVertex = minMaxVertex[0];
        String maxVertex = minMaxVertex[1];

//        System.out.println("MIN_VERTEX: " + minVertex);
//        System.out.println("MAX_VERTEX: " + maxVertex);
//        System.out.println("\033[31mEDGES_COUNT: " + graph.edgeSet().size() + "\033[0m");
//        System.out.println("\033[32mVERTICES_COUNT: " + graph.vertexSet().size() + "\033[0m");
        DijkstraManyToManyShortestPaths<String,DefaultWeightedEdge> dijkstraManyToManyShortestPaths = new DijkstraManyToManyShortestPaths<>(graph);
        Time.start();
        System.out.println(dijkstraManyToManyShortestPaths.getManyToManyPaths(graph.vertexSet(),graph.vertexSet()));
        Time.stopAndPrint();


    }
}
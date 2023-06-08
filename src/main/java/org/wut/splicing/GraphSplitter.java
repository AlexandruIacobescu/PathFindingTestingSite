package org.wut.splicing;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import static org.wut.Utility.addEdgesToGraph;
import static org.wut.Utility.addVerticesToGraph;

class Vertex{
    public String label;
    public double x;
    public double y;

    public Vertex(String label, double x, double y){
        this.label = label;
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "label= " + label + ", x= " + x + ",y= " + y;
    }
}

class Edge{
    public String sourceVertexLabel, targetVertexLabel;
    public double weight;

    public Edge(String sourcVertex, String targVertex, double weight){
        this.sourceVertexLabel = sourcVertex;
        this.targetVertexLabel = targVertex;
        this.weight = weight;
    }

    public String toString(){
        return "src= " + sourceVertexLabel + ", tgt= " + targetVertexLabel + ", weight= " + weight;
    }
}

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

    static ArrayList<Edge> edgesReader(String filename) throws FileNotFoundException{
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

    static Vertex getLeftMostvertex(List<Vertex> vertices){
        Vertex leftmost = vertices.get(0);
        for(var v : vertices){
            if(v.x < leftmost.x)
                leftmost = v;
        }
        return leftmost;
    }

    static Vertex geRightMostVertex(List<Vertex> vertices){
        Vertex rightmost = vertices.get(0);
        for(var v : vertices){
            if(v.x > rightmost.x)
                rightmost = v;
        }
        return rightmost;
    }

    static Vertex getBottomMostvertex(List<Vertex> vertices){
        Vertex bottommost = vertices.get(0);
        for(var v : vertices){
            if(v.y < bottommost.y)
                bottommost = v;
        }
        return bottommost;
    }

    static Vertex getUpMostvertex(List<Vertex> vertices){
        Vertex upmost = vertices.get(0);
        for(var v : vertices){
            if(v.y > upmost.y)
                upmost = v;
        }
        return upmost;
    }

    static ArrayList<Vertex> getVerticesKSplit(List<Vertex> vertices, double k, double xMin, double yMin, double distXminXmax, double distYminYmax){
        ArrayList<Vertex> result = new ArrayList<>();
        for(var v : vertices){
            if(v.x >= xMin && v.x <= xMin + distXminXmax/k && v.y <= yMin + distYminYmax/k && v.y >= yMin)
                result.add(v);
        }
        return result;
    }

    static double getDistXminXmax(double xMin, double xMax){
        if(xMin < 0 && xMax < 0)
            return Math.abs(xMin - xMax);
        if(xMin < 0 && xMax >= 0)
            return -xMin + xMax;
        if(xMin >= 0 && xMax >= 0)
            return xMax - xMin;
        return 0;
    }

    static double getDistYminYmax(double yMin, double yMax){
        if(yMin < 0 && yMax < 0)
            return Math.abs(yMin - yMax);
        if(yMin < 0 && yMax >= 0)
            return -yMin + yMax;
        if(yMin >= 0 && yMax >= 0)
            return yMax - yMin;
        return 0;
    }

    static void flushKSplit(ArrayList<Vertex> vertices, double xMin, double xMax, double yMin, double yMax, double k, String filename) throws IOException{
        double xMinXmaxDist = getDistXminXmax(xMin, xMax);
        double yMinYmaxDist = getDistYminYmax(yMin, yMax);
        ArrayList<Vertex> result = getVerticesKSplit(vertices, k, xMin, yMin, xMinXmaxDist, yMinYmaxDist);

        FileWriter writer = new FileWriter(new File(filename));

        for(var v : result)
            writer.write(v.toString() + "\n");

        writer.close();
    }

    static ArrayList<Vertex> getKSplitVertices(ArrayList<Vertex> vertices, double xMin, double xMax, double yMin, double yMax, double k){
        double xMinXmaxDist = getDistXminXmax(xMin, xMax);
        double yMinYmaxDist = getDistYminYmax(yMin, yMax);
        return getVerticesKSplit(vertices, k, xMin, yMin, xMinXmaxDist, yMinYmaxDist);
    }

    static ArrayList<Edge> getEdgesInKSplit(ArrayList<Vertex> vertices, ArrayList<Edge> edges) {
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

    public static DefaultDirectedGraph<String, DefaultWeightedEdge> createGraphFromKSPlit(String verticesCoordinatesFile, String weightedEdgesFile, double k) throws FileNotFoundException {
        DefaultDirectedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        ArrayList<Vertex> vertices;
        ArrayList<Edge> edges;
        ArrayList<Edge> edgesInKSplit;
        ArrayList<Vertex> verticesInKSplit;
        ArrayList<String> verticesLabels = new ArrayList<>();
        ArrayList<String[]> stringWeightedEdges = new ArrayList<>();

        vertices = vertexReader(verticesCoordinatesFile);
        edges = edgesReader(weightedEdgesFile);

        double xMin = getLeftMostvertex(vertices).x;
        double xMax = geRightMostVertex(vertices).x;
        double yMin = getBottomMostvertex(vertices).y;
        double yMax = getUpMostvertex(vertices).y;

        double distXminXmax = getDistXminXmax(xMin, xMax);
        double distYminYmax = getDistYminYmax(yMin, yMax);

        verticesInKSplit = getVerticesKSplit(vertices, k, xMin, yMin, distXminXmax, distYminYmax);
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

    public static void main(String[] args) throws Exception {
        ArrayList<Vertex> vertices = vertexReader("data/NYC/USA-road-d.NY.co.txt");
        ArrayList<Edge> edges = edgesReader("data/NYC/USA-road-d.NY.gr.txt");

        double xMin = getLeftMostvertex(vertices).x;
        double xMax = geRightMostVertex(vertices).x;
        double yMin = getBottomMostvertex(vertices).y;
        double yMax = getUpMostvertex(vertices).y;

        ArrayList<Edge> edgesInKSplit = getEdgesInKSplit(getKSplitVertices(vertices, xMin, xMax, yMin, yMax, 9), edges);

        FileWriter writer = new FileWriter(new File("data/splitgraph.txt"));

        for(var e : edgesInKSplit)
            writer.write(e.sourceVertexLabel + " " + e.targetVertexLabel + " " + e.weight + "\n");

        writer.close();
    }

}

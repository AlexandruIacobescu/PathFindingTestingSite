package org.wut;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.util.Pair;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class EuclideanDistanceAdmissibleHeuristic<V, E> implements AStarAdmissibleHeuristic<V> {

    private Map<V, Pair<Double, Double>> vertexCoordinate;

    private double getDistance(double xa, double ya, double xb, double yb){
        return Math.sqrt(Math.pow(xb - xa, 2) + Math.pow(yb - ya, 2));
    }

    public EuclideanDistanceAdmissibleHeuristic(Graph<V, E> graph, FileInputStream coordinatesFis){
        vertexCoordinate = new HashMap<>();
        Scanner scanner = new Scanner(coordinatesFis);
        while(scanner.hasNextLine()){
            String[] vxy = scanner.nextLine().split(" ");
            double x = Double.parseDouble(vxy[1]);
            double y = Double.parseDouble(vxy[2]);
            Pair<Double,Double> xy = new Pair<>(x,y);
            vertexCoordinate.put((V)vxy[0], xy);
        }
        scanner.close();
    }
    @Override
    public double getCostEstimate(V sourceVertex, V targetVertex) {
        Pair<Double,Double> sc, tc;
        sc = vertexCoordinate.get(sourceVertex);
        tc = vertexCoordinate.get(targetVertex);
        return getDistance(sc.getFirst(), sc.getSecond(), tc.getFirst(), tc.getSecond());
    }
}

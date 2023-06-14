package org.wut.splicing;

public class Edge{
    public String sourceVertexLabel, targetVertexLabel;
    public double weight;

    public Edge(String sourceVertex, String targetVertex, double weight){
        this.sourceVertexLabel = sourceVertex;
        this.targetVertexLabel = targetVertex;
        this.weight = weight;
    }

    public String toString(){
        return "src= " + sourceVertexLabel + ", tgt= " + targetVertexLabel + ", weight= " + weight;
    }
}
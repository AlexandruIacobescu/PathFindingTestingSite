package org.wut.splicing;

public class Vertex{
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
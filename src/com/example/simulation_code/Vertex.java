package com.example.simulation_code;

public class Vertex {
    Elements element;
    public boolean wasVisited;

    public Vertex(Elements element) {
        wasVisited = false;
        this.element = element;
    }

}

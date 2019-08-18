package com.example.simulation_code.Graph;

import com.example.simulation_code.Elements.Elements;

public class Vertex {
    public Elements element;
    public boolean wasVisited;

    public Vertex(Elements element) {
        wasVisited = false;
        this.element = element;
    }

}

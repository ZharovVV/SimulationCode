package com.example.simulation_code;

import com.example.simulation_code.Elements.Elements;

public class Vertex {
    Elements element;
    public boolean wasVisited;

    public Vertex(Elements element) {
        wasVisited = false;
        this.element = element;
    }

}
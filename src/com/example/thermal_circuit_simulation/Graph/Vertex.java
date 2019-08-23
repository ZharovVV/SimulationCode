package com.example.thermal_circuit_simulation.Graph;

import com.example.thermal_circuit_simulation.Elements.Elements;

public class Vertex {
    public Elements element;
    public boolean wasVisited;

    public Vertex(Elements element) {
        wasVisited = false;
        this.element = element;
    }

}

package com.example.thermal_circuit_simulation.Graph;

import com.example.thermal_circuit_simulation.Elements.Element;

public class Vertex {
    public Element element;
    public boolean wasVisited;

    public Vertex(Element element) {
        wasVisited = false;
        this.element = element;
    }

}

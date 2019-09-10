package com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces;

import com.example.thermal_circuit_simulation.Elements.Element;

public class Equation {
    private Element element;

    public Equation(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return element;
    }
}

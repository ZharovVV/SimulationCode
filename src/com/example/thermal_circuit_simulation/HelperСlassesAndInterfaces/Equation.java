package com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces;

import com.example.thermal_circuit_simulation.Elements.Elements;

public class Equation {
    private Elements element;

    public Equation(Elements element) {
        this.element = element;
    }

    public Elements getElement() {
        return element;
    }
}

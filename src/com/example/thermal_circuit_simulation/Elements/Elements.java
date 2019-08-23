package com.example.thermal_circuit_simulation.Elements;

import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Describable;

public class Elements implements Describable {
    public final String NAME;

    public Elements(String name) {
        this.NAME = name;
    }


    @Override
    public void describe() {
        System.out.println("Параметры " + NAME + " :");
    }
}


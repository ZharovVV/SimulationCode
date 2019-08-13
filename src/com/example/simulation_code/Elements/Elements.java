package com.example.simulation_code.Elements;

import com.example.simulation_code.HelperСlassesAndInterfaces.Describable;

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


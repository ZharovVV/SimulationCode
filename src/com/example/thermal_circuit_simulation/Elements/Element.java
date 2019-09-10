package com.example.thermal_circuit_simulation.Elements;

import com.example.thermal_circuit_simulation.Graph.Graph;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Calculation;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Describable;
import com.example.thermal_circuit_simulation.ThermalEfficiencyIndicators.ThermalEfficiencyIndicators;

public abstract class Element implements Describable, Calculation {
    public final String NAME;
    public Element(String name) {
        this.NAME = name;
    }

    @Override
    public void describe() {
        System.out.println("Параметры " + NAME + " :");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
    }

    @Override
    public void calculationOfInitialParameters(int v, Graph theGraph) {

    }

    @Override
    public void setSelectionNumber(int selectionNumber) {

    }

    @Override
    public void calculationOfThermalEfficiencyIndicators(int v, ThermalEfficiencyIndicators thermalEfficiencyIndicators, Graph theGraph) {

    }
}


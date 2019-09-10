package com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces;

import com.example.thermal_circuit_simulation.Graph.Graph;
import com.example.thermal_circuit_simulation.ThermalEfficiencyIndicators.ThermalEfficiencyIndicators;

public interface Calculation {
    void calculationOfInitialParameters(int v, Graph theGraph);

    void setSelectionNumber(int selectionNumber);

    void calculationOfThermalEfficiencyIndicators(int v, ThermalEfficiencyIndicators thermalEfficiencyIndicators, Graph theGraph);
}

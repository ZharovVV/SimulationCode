package com.example.thermal_circuit_simulation.Elements;

import com.example.thermal_circuit_simulation.Graph.Graph;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.CalculationOfThermalEfficiencyIndicators;
import com.example.thermal_circuit_simulation.ThermalEfficiencyIndicators.ThermalEfficiencyIndicators;
import com.hummeling.if97.IF97;

public class HeatNetwork extends Elements implements CalculationOfThermalEfficiencyIndicators {
    private double inletTemperature;            // Температура на входе в ТС
    private double inletPressure;               // Давление на входе в ТС
    private double inletEnthalpy;               // Энтальпия на входе в ТС
    private double outletTemperature;           // Температура на выходе из ТС
    private double outletPressure;              // Давление на выходе из ТС
    private double outletEnthalpy;              // Энтальпия на выходе из ТС
    private double thermalPowerOfHeatNetwork;    // Тепловая мощность тепловой сети
    private double networkWaterConsumption;      // Расход сетевой воды

    public HeatNetwork(String name, double inletPressure, double inletTemperature, double outletPressure, double outletTemperature, double thermalPowerOfHeatNetwork) {
        super(name);
        this.outletPressure = outletPressure;
        this.outletTemperature = outletTemperature;
        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
        this.outletEnthalpy = waterSteam.specificEnthalpyPT(outletPressure, outletTemperature + 273.15);
        this.thermalPowerOfHeatNetwork = thermalPowerOfHeatNetwork;
        this.inletPressure = inletPressure;
        this.inletTemperature = inletTemperature;
        this.inletEnthalpy = waterSteam.specificEnthalpyPT(inletPressure, inletTemperature + 273.15);
        this.networkWaterConsumption = thermalPowerOfHeatNetwork * 1000 / (inletEnthalpy - outletEnthalpy);
    }

    public double getOutletTemperature() {
        return outletTemperature;
    }

    public double getOutletPressure() {
        return outletPressure;
    }

    public double getOutletEnthalpy() {
        return outletEnthalpy;
    }

    public double getNetworkWaterConsumption() {
        return networkWaterConsumption;
    }

    @Override
    public void describe() {
        super.describe();
        System.out.println("Параметры на входе в ТС:");
        System.out.println("Давление: " + inletPressure + " ,МПа");
        System.out.println("Температура: " + inletTemperature + " ,℃");
        System.out.println("Энтальпия: " + inletEnthalpy + " ,кДж/кг");
        System.out.println();
        System.out.println("Параметры на выходе из ТС:");
        System.out.println("Давление: " + outletPressure + " ,МПа");
        System.out.println("Температура: " + outletTemperature + " ,℃");
        System.out.println("Энтальпия: " + outletEnthalpy + " ,кДж/кг");
        System.out.println("Расход сетевой воды: " + networkWaterConsumption + " ,кг/c");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    @Override
    public void calculationOfThermalEfficiencyIndicators(int v, ThermalEfficiencyIndicators thermalEfficiencyIndicators, Graph theGraph) {
        thermalEfficiencyIndicators.setThermalPowerOfHeatNetwork(thermalPowerOfHeatNetwork);
    }
}

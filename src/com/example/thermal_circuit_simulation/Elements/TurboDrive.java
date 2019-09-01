package com.example.thermal_circuit_simulation.Elements;

import com.example.thermal_circuit_simulation.Graph.Graph;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.CalculationOfThermalEfficiencyIndicators;
import com.example.thermal_circuit_simulation.ThermalEfficiencyIndicators.ThermalEfficiencyIndicators;
import com.hummeling.if97.IF97;

public class TurboDrive extends Elements implements CalculationOfThermalEfficiencyIndicators {
    private double mechanicalEfficiency;        //Механический КПД Турбопривода
    private double relativeInternalEfficiency;  //Относительный внутренний КПД ТП
    private double condenserPressure;           // Давление в конденсаторе ТП
    private double turboPower;                // Мощность ТП
    private double steamConsumption;            // Расход пара через ТП
    private double inletEnthalpy;
    private double outletEnthalpy;
    private int selectionNumber;


    public TurboDrive(String name,
                      double mechanicalEfficiency,
                      double relativeInternalEfficiency,
                      double condenserPressure,
                      double feedwaterFlow,
                      Pumps feedPump,
                      Superheaters superheater) {
        // TODO: 01.09.2019 Нужен ли вообще этот конструктор?
        super(name);
        this.mechanicalEfficiency = mechanicalEfficiency;
        this.relativeInternalEfficiency = relativeInternalEfficiency;
        this.condenserPressure = condenserPressure;
        this.inletEnthalpy = superheater.getEnthalpyOfHeatedMedium();
        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
        this.outletEnthalpy =
                inletEnthalpy - relativeInternalEfficiency * (inletEnthalpy - waterSteam.specificEnthalpyPS(
                        condenserPressure,
                        waterSteam.specificEntropyPH(superheater.getPressureOfHeatedMedium(), superheater.getEnthalpyOfHeatedMedium())));
        this.turboPower = feedPump.getEnthalpyIncrease() * feedwaterFlow / 1000 / mechanicalEfficiency;
        this.steamConsumption = turboPower * 1000 / (inletEnthalpy - outletEnthalpy);
    }

    public TurboDrive(String name,
                      double relativeInternalEfficiency,
                      double condenserPressure,
                      double feedwaterFlow,
                      Pumps feedPump,
                      int selectionNumber,
                      TurbineCylinders turbineCylinder) {
        super(name);
        this.selectionNumber = selectionNumber;
        this.mechanicalEfficiency = feedPump.getPumpDriveEfficiency();
        this.relativeInternalEfficiency = relativeInternalEfficiency;
        this.condenserPressure = condenserPressure;
        TurbineCylinders.Parameters parameters = turbineCylinder.parametersInSelection(selectionNumber);
        this.inletEnthalpy = parameters.getEnthalpy();
        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
        this.outletEnthalpy =
                inletEnthalpy - relativeInternalEfficiency * (inletEnthalpy - waterSteam.specificEnthalpyPS(
                        condenserPressure,
                        waterSteam.specificEntropyPH(parameters.getPressure(), parameters.getEnthalpy())));
        this.turboPower = feedPump.getEnthalpyIncrease() * feedwaterFlow / 1000 / mechanicalEfficiency;
        this.steamConsumption = turboPower * 1000 / (inletEnthalpy - outletEnthalpy);
    }

    public double getSteamConsumption() {
        return steamConsumption;
    }

    public int getSelectionNumber() {
        return selectionNumber;
    }

    @Override
    public void describe() {
        super.describe();
        System.out.println("Механический КПД Турбопривода: " + mechanicalEfficiency);
        System.out.println("Относительный внутренний КПД ТП: " + relativeInternalEfficiency);
        System.out.println("Давление в конденсаторе ТП: " + condenserPressure + " ,МПа");
        System.out.println("Мощность ТП: " + turboPower + " ,МВт");
        System.out.println("Энтальпия на входе: " + inletEnthalpy + " ,кДж/кг");
        System.out.println("Энтальпия на выходе: " + outletEnthalpy + " ,кДж/кг");
        System.out.println("Расход пара через ТП: " + steamConsumption + " ,кг/c");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    @Override
    public void calculationOfThermalEfficiencyIndicators(int v, ThermalEfficiencyIndicators thermalEfficiencyIndicators, Graph theGraph) {
        thermalEfficiencyIndicators.setTurboPower(turboPower);
    }
}



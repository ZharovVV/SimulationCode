package com.example.simulation_code;

import com.hummeling.if97.IF97;

public class TurboDrive extends Elements {
    private double mechanicalEfficiency;    //Механический КПД Турбопривода
    private double relativeInternalEfficiency;  //Относительный внутренний КПД ТП
    private double condenserPressure;           // Давление в конденсаторе ТП
    private double turbinePower;                // Мощность ТП
    private double steamConsumption;            // Расход пара через ТП
    private double inletEnthalpy;
    private double outletEnthalpy;


    public TurboDrive(String name,
                      double mechanicalEfficiency,
                      double relativeInternalEfficiency,
                      double condenserPressure,
                      double feedwaterFlow,
                      Pumps feedPump,
                      Superheaters superheater) {
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
        this.turbinePower = feedPump.getEnthalpyIncrease() * feedwaterFlow / 1000 / mechanicalEfficiency;
    }

    public TurboDrive(String name,
                      double mechanicalEfficiency,
                      double relativeInternalEfficiency,
                      double condenserPressure,
                      double feedwaterFlow,
                      Pumps feedPump,
                      TurbineCylinders turbineCylinder,
                      int selectionNumber) {
        super(name);
        this.mechanicalEfficiency = mechanicalEfficiency;
        this.relativeInternalEfficiency = relativeInternalEfficiency;
        this.condenserPressure = condenserPressure;
        Parameters parameters = turbineCylinder.parametersInSelection(selectionNumber);
        this.inletEnthalpy = parameters.getEnthalpy();
        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
        this.outletEnthalpy =
                inletEnthalpy - relativeInternalEfficiency * (inletEnthalpy - waterSteam.specificEnthalpyPS(
                        condenserPressure,
                        waterSteam.specificEntropyPH(parameters.getPressure(), parameters.getEnthalpy())));
        this.turbinePower = feedPump.getEnthalpyIncrease() * feedwaterFlow / 1000 / mechanicalEfficiency;
    }

    public void describeTurboDrive() {
        System.out.println("Параметры " + NAME + " :");
        System.out.println("Механический КПД Турбопривода: " + mechanicalEfficiency);
        System.out.println("Относительный внутренний КПД ТП: " + relativeInternalEfficiency);
        System.out.println("Давление в конденсаторе ТП: " + condenserPressure + " ,МПа");
        System.out.println("Мощность ТП: " + turbinePower + " ,МВт");
        System.out.println("Энтальпия на входе: " + inletEnthalpy + " ,кДж/кг");
        System.out.println("Энтальпия на выходе: " + outletEnthalpy + " ,кДж/кг");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println();
    }
}



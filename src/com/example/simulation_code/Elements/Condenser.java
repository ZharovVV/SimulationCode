package com.example.simulation_code.Elements;

import com.hummeling.if97.IF97;

public class Condenser extends Elements {
    //-----------------------------Характеристики греющего пара---------------------------------------------------------
    private double pressureOfHeatingSteam;                      // Давление греющего пара на входе в конденсатор
    private double temperatureOfHeatingSteam;                   // Температура греющего пара на входе в конденсатор
    private double enthalpyOfHeatingSteam;                      // Энтальпия греющего пара на входе в конденсатор
    private double consumptionOfHeatingSteam;
    //-----------------------------Характеристики дренажа пара----------------------------------------------------------
    private double pressureOfSteamDrain;                        // Давление дренажа пара на выходе из конденсатора
    private double temperatureOfSteamDrain;                     // Температура дренажа пара на выходе из конденсатора
    private double enthalpyOfSteamDrain;                        // Энтальпия дренажа пара на выходе из конденсатора
    private double consumptionOfSteamDrain;

    public Condenser(String name, TurbineCylinders turbineCylinder) {
        super(name);
        this.pressureOfHeatingSteam = turbineCylinder.parametersInSelection(turbineCylinder.NUMBER_OF_SELECTIONS + 1).getPressure();    // Давление в конденсаторе ( = давлению на выходе из цилиндра)
        this.enthalpyOfHeatingSteam = turbineCylinder.parametersInSelection(turbineCylinder.NUMBER_OF_SELECTIONS + 1).getEnthalpy();    // Энтальпия пара на входе в конденсатор

        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);

        this.temperatureOfHeatingSteam = waterSteam.saturationTemperatureP(pressureOfHeatingSteam) - 273.15;
        this.pressureOfSteamDrain = pressureOfHeatingSteam;
        this.temperatureOfSteamDrain = temperatureOfHeatingSteam;
        this.enthalpyOfSteamDrain = waterSteam.specificEnthalpySaturatedLiquidP(pressureOfHeatingSteam);
    }

    public double getPressureOfSteamDrain() {
        return pressureOfSteamDrain;
    }

    public double getTemperatureOfSteamDrain() {
        return temperatureOfSteamDrain;
    }

    public double getEnthalpyOfSteamDrain() {
        return enthalpyOfSteamDrain;
    }

    public double getConsumptionOfHeatingSteam() {
        return consumptionOfHeatingSteam;
    }

    public void setConsumptionOfHeatingSteam(double consumptionOfHeatingSteam) {
        this.consumptionOfHeatingSteam = consumptionOfHeatingSteam;
    }

    public double getConsumptionOfSteamDrain() {
        return consumptionOfSteamDrain;
    }

    public void setConsumptionOfSteamDrain(double consumptionOfSteamDrain) {
        this.consumptionOfSteamDrain = consumptionOfSteamDrain;
    }

    public void describeCondenser() {
        System.out.println("Параметры " + NAME + " :");
        System.out.println("Параметры на входе в конденсатор:");
        System.out.println("Давление: " + pressureOfHeatingSteam + " ,МПа");
        System.out.println("Температура: " + temperatureOfHeatingSteam + " ,℃");
        System.out.println("Энтальпия: " + enthalpyOfHeatingSteam + " ,кДж/кг");
        System.out.println();
        System.out.println("Параметры на выходе из конденсатора:");
        System.out.println("Давление: " + pressureOfSteamDrain + " ,МПа");
        System.out.println("Температура: " + temperatureOfSteamDrain + " ,℃");
        System.out.println("Энтальпия: " + enthalpyOfSteamDrain + " ,кДж/кг");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println();
    }
}

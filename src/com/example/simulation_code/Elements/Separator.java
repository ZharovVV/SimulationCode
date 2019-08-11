package com.example.simulation_code.Elements;

import com.example.simulation_code.HelperСlasses.Consumptions;
import com.example.simulation_code.HelperСlasses.Equation;
import com.hummeling.if97.IF97;

public class Separator extends Elements {
    //------------------------------Характеристики сепаратора-----------------------------------------------------------
    private double hydraulicResistanceFromCylinderToSeparator;    // Гидравлическое сопротивление от отбора до сепаратора
    private double outletDegreeOfDryness;                         // Степень сухости на выходе из сепаратора
    //-----------------------------Характеристики греющего пара---------------------------------------------------------
    private double pressureOfHeatingSteam;                      // Давление греющего пара на входе в сепаратор
    private double temperatureOfHeatingSteam;                   // Температура греющего пара на входе в сепаратор
    private double enthalpyOfHeatingSteam;                      // Энтальпия греющего пара на входе в сепаратор
    private Consumptions consumptionOfHeatingSteam = new Consumptions();
    //-----------------------------Характеристики дренажа пара----------------------------------------------------------
    private double pressureOfSteamDrain;                        // Давление дренажа пара на выходе из сепаратора
    private double temperatureOfSteamDrain;                     // Температура дренажа пара на выходе из сепаратора
    private double enthalpyOfSteamDrain;                        // Энтальпия дренажа пара на выходе из сепаратора
    private Consumptions consumptionOfSteamDrain = new Consumptions();                       // Расход дренажа пара на выходе из сепаратора
    //-----------------------------Характеристики обогреваемой среды на выходе------------------------------------------
    private double pressureOfHeatedMedium;                      // Давление сепарируемой среды на выходе из сепаратора
    private double temperatureOfHeatedMedium;                   // Температура сепарируемой среды на выходе из сепаратора
    private double enthalpyOfHeatedMedium;                      // Энтальпия сепарируемой среды на выходе из сепаратора
    private Consumptions consumptionOfHeatedMedium = new Consumptions();

    private Equation materialBalanceEquation = new Equation();
    private Equation heatBalanceEquation = new Equation();

    public Separator(String name, double hydraulicResistanceFromCylinderToSeparator, double outletDegreeOfDryness, TurbineCylinders turbineCylinder) {
        super(name);
        this.hydraulicResistanceFromCylinderToSeparator = hydraulicResistanceFromCylinderToSeparator;
        this.outletDegreeOfDryness = outletDegreeOfDryness;
        this.pressureOfHeatingSteam =
                turbineCylinder.parametersInSelection(turbineCylinder.NUMBER_OF_SELECTIONS + 1).getPressure() - hydraulicResistanceFromCylinderToSeparator;
        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
        this.temperatureOfHeatingSteam = waterSteam.saturationTemperatureP(pressureOfHeatingSteam) - 273.15;
        this.enthalpyOfHeatingSteam = turbineCylinder.parametersInSelection(turbineCylinder.NUMBER_OF_SELECTIONS + 1).getEnthalpy();
        this.pressureOfSteamDrain = pressureOfHeatingSteam;
        this.temperatureOfSteamDrain = temperatureOfHeatingSteam;
        this.enthalpyOfSteamDrain = waterSteam.specificEnthalpySaturatedLiquidP(pressureOfSteamDrain);
        this.pressureOfHeatedMedium = pressureOfSteamDrain;
        this.temperatureOfHeatedMedium = temperatureOfHeatingSteam;
        this.enthalpyOfHeatedMedium = waterSteam.specificEnthalpyPX(pressureOfHeatingSteam,outletDegreeOfDryness);
    }

    public double getPressureOfHeatedMedium() {
        return pressureOfHeatedMedium;
    }

    public double getTemperatureOfHeatedMedium() {
        return temperatureOfHeatedMedium;
    }

    public double getEnthalpyOfHeatingSteam() {
        return enthalpyOfHeatingSteam;
    }

    public double getEnthalpyOfSteamDrain() {
        return enthalpyOfSteamDrain;
    }

    public double getEnthalpyOfHeatedMedium() {
        return enthalpyOfHeatedMedium;
    }

    public Consumptions getConsumptionOfHeatingSteam() {
        return consumptionOfHeatingSteam;
    }

    public Consumptions getConsumptionOfSteamDrain() {
        return consumptionOfSteamDrain;
    }

    public Consumptions getConsumptionOfHeatedMedium() {
        return consumptionOfHeatedMedium;
    }

    public Equation getMaterialBalanceEquation() {
        return materialBalanceEquation;
    }

    public Equation getHeatBalanceEquation() {
        return heatBalanceEquation;
    }

    public void describeSeparator(){
        System.out.println("Параметры в " + NAME + " :");
        System.out.println("-----------------------------Характеристики подогревателя---------------------------------------------------------");
        System.out.println("Гидравлическое сопротивление от отбора до сепаратора: " + hydraulicResistanceFromCylinderToSeparator + " ,МПа");
        System.out.println("Степень сухости на выходе из сепаратора: " + outletDegreeOfDryness);
        System.out.println("-----------------------------Характеристики греющего пара---------------------------------------------------------");
        System.out.println("Давление греющего пара на входе в подогреватель: " + pressureOfHeatingSteam + " ,МПа");
        System.out.println("Температура греющего пара на входе в подогреватель: " + temperatureOfHeatingSteam + " ,℃");
        System.out.println("Энтальпия греющего пара на входе в подогреватель: " + enthalpyOfHeatingSteam + " ,кДж/кг");
        System.out.println("-----------------------------Характеристики дренажа пара----------------------------------------------------------");
        System.out.println("Давление дренажа пара на выходе из подогревателя: " + pressureOfSteamDrain + " ,МПа");
        System.out.println("Температура дренажа пара на выходе из подогревателя: " + temperatureOfSteamDrain + " ,℃");
        System.out.println("Энтальпия дренажа пара на выходе из подогревателя: " + enthalpyOfSteamDrain + " ,кДж/кг");
        System.out.println("-----------------------------Характеристики обогреваемой среды на выходе------------------------------------------");
        System.out.println("Давление обогреваемой среды на выходе из подогревателя: " + pressureOfHeatedMedium + " ,МПа");
        System.out.println("Температура обогреваемой среды на выходе из подогревателя: " + temperatureOfHeatedMedium + " ,℃");
        System.out.println("Энтальпия обогреваемой среды на выходе из подогревателя: " + enthalpyOfHeatedMedium + " ,кДж/кг");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }
}

package com.example.simulation_code.Elements;

import com.hummeling.if97.IF97;

public class Pumps extends Elements {

    private double efficiency;              // КПД насоса
    private double pumpHead;                // Необходимый напор насоса

    private double inletTemperature;        // Температура на входе в насос
    private double inletPressure;           // Давление на входе в насос
    private double inletEnthalpy;           // Энтальпия на входе в насос
    private double enthalpyIncrease;        // Повышение энтальпии в насосе
    private double outletTemperature;       // Температура на выходе из насоса
    private double outletPressure;          // Давление на выходе из насоса
    private double outletEnthalpy;          // Энтальпия на выходе из насоса

    public Pumps(String name, double efficiency, double pumpHead, Elements previousElement) {
        super(name);
        this.efficiency = efficiency;
        this.pumpHead = pumpHead;
        if (previousElement.getClass() == Heaters.class) {                          // Если предыдущий элемент - подогреватель
            Heaters previousHeater = (Heaters) previousElement;
            this.inletTemperature = previousHeater.getTemperatureOfHeatedMedium();
            this.inletPressure = previousHeater.getPressureOfHeatedMedium();
            this.inletEnthalpy = previousHeater.getEnthalpyOfHeatedMedium();
        } else {                                                                    // Если предыдущий элемент - конденсатор
            Condenser condenser = (Condenser) previousElement;
            this.inletTemperature = condenser.getTemperatureOfSteamDrain();
            this.inletPressure = condenser.getPressureOfSteamDrain();
            this.inletEnthalpy = condenser.getEnthalpyOfSteamDrain();
        }
        this.outletTemperature = inletTemperature;
        this.outletPressure = inletPressure + pumpHead;

        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);

        this.enthalpyIncrease =
                pumpHead * waterSteam.specificVolumePT((inletPressure + outletPressure) / 2, inletTemperature + 273.15) * 1000 / efficiency;
        this.outletEnthalpy = inletEnthalpy + enthalpyIncrease;
    }

    public double getOutletTemperature() {
        return outletTemperature;
    }

    public double getOutletPressure() {
        return outletPressure;
    }

    public double getEnthalpyIncrease() {
        return enthalpyIncrease;
    }

    public void describePump() {
        System.out.println("Параметры " + NAME + " :");
        System.out.println("КПД насоса: " + efficiency);
        System.out.println("Напор насоса: " + pumpHead + " ,МПа");
        System.out.println("Повышение энтальпии в насосе: " + enthalpyIncrease + " ,кДж/кг");
        System.out.println("Параметры на входе в насос:");
        System.out.println("Давление: " + inletPressure + " ,МПа");
        System.out.println("Температура: " + inletTemperature + " ,℃");
        System.out.println("Энтальпия: " + inletEnthalpy + " ,кДж/кг");
        System.out.println();
        System.out.println("Параметры на выходе из насоса:");
        System.out.println("Давление: " + outletPressure + " ,МПа");
        System.out.println("Температура: " + outletTemperature + " ,℃");
        System.out.println("Энтальпия: " + outletEnthalpy + " ,кДж/кг");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println();
    }
}

package com.example.simulation_code;

import com.hummeling.if97.IF97;

public class Parameters {
    private double pressure;
    private double temperature;
    private double degreeOfDryness;
    private double enthalpy;

    public Parameters(double pressure, double temperatureOrDegreeOfDryness) {
        this.pressure = pressure;
        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
        if (temperatureOrDegreeOfDryness > 1) {
            this.temperature = temperatureOrDegreeOfDryness;
            this.degreeOfDryness = Double.NaN;
            this.enthalpy = waterSteam.specificEnthalpyPT(pressure,temperatureOrDegreeOfDryness + 273.15);
        } else {
            this.temperature = Double.NaN;
            this.degreeOfDryness = temperatureOrDegreeOfDryness;
            this.enthalpy = waterSteam.specificEnthalpyPX(pressure,temperatureOrDegreeOfDryness);
        }
    }

    public double getPressure() {
        return pressure;
    }

    public double getEnthalpy() {
        return enthalpy;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getDegreeOfDryness() {
        return degreeOfDryness;
    }
}

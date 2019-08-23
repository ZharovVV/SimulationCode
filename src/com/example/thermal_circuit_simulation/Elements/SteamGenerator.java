package com.example.thermal_circuit_simulation.Elements;

public class SteamGenerator extends Elements{
    private double steamСonsumption;

    public SteamGenerator(String name, double steamСonsumption) {
        super(name);
        this.steamСonsumption = steamСonsumption;
    }

    public double getSteamСonsumption() {
        return steamСonsumption;
    }

    @Override
    public void describe() {
        super.describe();
        System.out.println("Расход пара через ПГ: " + steamСonsumption + " ,кг/с");
        System.out.println();
    }
}

package com.example.simulation_code.Elements;

public class SteamGenerator extends Elements{
    private double steamСonsumption;

    public SteamGenerator(String name, double steamСonsumption) {
        super(name);
        this.steamСonsumption = steamСonsumption;
    }

    public double getSteamСonsumption() {
        return steamСonsumption;
    }
}

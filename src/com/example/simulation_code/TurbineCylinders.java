package com.example.simulation_code;

import java.util.ArrayList;

public class TurbineCylinders extends Elements {
    public final int NUMBER_OF_SELECTIONS;                                                  // Число отборов в турбине
    private ArrayList<Parameters> listOfParametersInSelections;                             // Список параметров отбора, включая параметры на входе и выходе из цилиндра
    private double inletSteamConsumption;
    private double outletSteamConsumption;

    public TurbineCylinders(String name, int numberOfSelections) {
        super(name);
        this.NUMBER_OF_SELECTIONS = numberOfSelections;
        listOfParametersInSelections = new ArrayList<>(numberOfSelections + 2);
    }

    //------------------------------------------------------------------------------------------------------------------
    //Метод добавляет параметры отбора
    // numberOfSelection = 0 - параметры на входе в цилиндр
    // numberOfSelection + 2 - параметры ны выходе из цилиндра
    public void addSelection(int selectionNumber, double pressure, double XorT) {
        listOfParametersInSelections.add(selectionNumber, new Parameters(pressure, XorT));
    }

    public Parameters parametersInSelection(int selectionNumber) {
        return listOfParametersInSelections.get(selectionNumber);
    }

    public void describeTurbineCylinder() {
        System.out.println("Параметры " + NAME + " :");
        for (Parameters parameters : listOfParametersInSelections) {
            int i = listOfParametersInSelections.indexOf(parameters);
            if (i == 0) {
                System.out.println("Параметры на входе в цилиндр:");
                System.out.println("Давление: " + parameters.getPressure() + " ,МПа");
                System.out.println("Температура: " + parameters.getTemperature() + " ,℃");
                System.out.println("Степень сухости: " + parameters.getDegreeOfDryness());
                System.out.println("Энтальпия: " + parameters.getEnthalpy() + " ,кДж/кг");
                System.out.println();
            } else if (i == NUMBER_OF_SELECTIONS + 1) {
                System.out.println("Параметры на выходе из цилиндра:");
                System.out.println("Давление: " + parameters.getPressure() + " ,МПа");
                System.out.println("Температура: " + parameters.getTemperature() + " ,℃");
                System.out.println("Степень сухости: " + parameters.getDegreeOfDryness());
                System.out.println("Энтальпия: " + parameters.getEnthalpy() + " ,кДж/кг");
                System.out.println();
            } else {
                System.out.println("Номер отбора:" + i);
                System.out.println("Давление: " + parameters.getPressure() + " ,МПа");
                System.out.println("Температура: " + parameters.getTemperature() + " ,℃");
                System.out.println("Степень сухости: " + parameters.getDegreeOfDryness());
                System.out.println("Энтальпия: " + parameters.getEnthalpy() + " ,кДж/кг");
                System.out.println();
            }
        }
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println();
    }

    public double getInletSteamConsumption() {
        return inletSteamConsumption;
    }

    public void setInletSteamConsumption(double inletSteamConsumption) {
        this.inletSteamConsumption = inletSteamConsumption;
    }

    public double getOutletSteamConsumption() {
        return outletSteamConsumption;
    }

    public void setOutletSteamConsumption(double outletSteamConsumption) {
        this.outletSteamConsumption = outletSteamConsumption;
    }
}

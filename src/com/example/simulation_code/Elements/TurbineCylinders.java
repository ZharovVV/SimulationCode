package com.example.simulation_code.Elements;

import com.example.simulation_code.HelperСlasses.Equation;
import com.hummeling.if97.IF97;

import java.util.ArrayList;

public class TurbineCylinders extends Elements {
    public final int NUMBER_OF_SELECTIONS;                                                  // Число отборов в турбине
    private ArrayList<Parameters> listOfParametersInSelections;                             // Список параметров отбора, включая параметры на входе и выходе из цилиндра
    private double inletSteamConsumption;
    private double outletSteamConsumption;

    private Equation materialBalanceEquation = new Equation();

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

    public Equation getMaterialBalanceEquation() {
        return materialBalanceEquation;
    }

    public static class Parameters {
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
}

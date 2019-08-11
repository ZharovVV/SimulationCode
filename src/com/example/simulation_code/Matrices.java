package com.example.simulation_code;

import com.example.simulation_code.Elements.*;
import com.example.simulation_code.HelperСlasses.Consumptions;
import com.example.simulation_code.HelperСlasses.Equation;

import java.util.ArrayList;


public class Matrices {
    public double[][] coefficientMatrix;
    public double[] freeMemoryMatrix;
    private ArrayList<Consumptions> listOfConsumptions;
    private ArrayList<Equation> listOfEquation;

    @SuppressWarnings("ConstantConditions")
    public Matrices(ArrayList<Vertex> vertexList) {
        listOfConsumptions = new ArrayList<>();
        listOfEquation = new ArrayList<>();
        int i = 0;
        int j = 0;

        for (Vertex vertex : vertexList) {
            Elements element = vertex.element;
            if (element.getClass() == Heaters.class) {
                Heaters heater = (Heaters) element;
                if (heater.isSurfaceHeater()) {
                    i = i + 3;
                    j = j + 3;
                    listOfConsumptions.add(heater.getConsumptionOfHeatingSteam());
                    listOfConsumptions.add(heater.getConsumptionOfSteamDrain());
                    listOfConsumptions.add(heater.getConsumptionOfHeatedMedium());
                    listOfEquation.add(heater.getMaterialBalanceEquationOnSteamDrainLine());
                    listOfEquation.add(heater.getMaterialBalanceEquationOnHeatedMediumLine());
                    listOfEquation.add(heater.getHeatBalanceEquation());
                } else {
                    i = i + 2;
                    j = j + 2;
                    listOfConsumptions.add(heater.getConsumptionOfHeatingSteam());
                    listOfConsumptions.add(heater.getConsumptionOfHeatedMedium());
                    listOfEquation.add(heater.getMaterialBalanceEquationOnHeatedMediumLine());
                    listOfEquation.add(heater.getHeatBalanceEquation());
                }
            }


            if (element.getClass() == Superheaters.class) {
                Superheaters superheater = (Superheaters) element;
                i = i + 3;
                j = j + 3;
                listOfConsumptions.add(superheater.getConsumptionOfHeatingSteam());
                listOfConsumptions.add(superheater.getConsumptionOfSteamDrain());
                listOfConsumptions.add(superheater.getConsumptionOfHeatedMedium());
                listOfEquation.add(superheater.getMaterialBalanceEquationOnSteamDrainLine());
                listOfEquation.add(superheater.getMaterialBalanceEquationOnHeatedMediumLine());
                listOfEquation.add(superheater.getHeatBalanceEquation());
            }

            if (element.getClass() == Separator.class) {
                Separator separator = (Separator) element;
                i = i + 2;
                j = j + 3;
                listOfConsumptions.add(separator.getConsumptionOfHeatingSteam());
                listOfConsumptions.add(separator.getConsumptionOfSteamDrain());
                listOfConsumptions.add(separator.getConsumptionOfHeatedMedium());
                listOfEquation.add(separator.getMaterialBalanceEquation());
                listOfEquation.add(separator.getHeatBalanceEquation());

            }

            if (element.getClass() == TurbineCylinders.class) {
                TurbineCylinders turbineCylinder = (TurbineCylinders) element;
                i = i + 1;
                listOfEquation.add(turbineCylinder.getMaterialBalanceEquation());

            }

            if (element.getClass() == Condenser.class) {
                Condenser condenser = (Condenser) element;
                i = i + 1;
                j = j + 2;
                listOfConsumptions.add(condenser.getConsumptionOfHeatingSteam());
                listOfConsumptions.add(condenser.getConsumptionOfSteamDrain());
                listOfEquation.add(condenser.getMaterialBalanceEquation());
            }
        }
        /*System.out.println("Число уравнений: " + i + " Число неизвестных: " + j);*/
        coefficientMatrix = new double[i][j];
        freeMemoryMatrix = new double[i];
    }

    public ArrayList<Consumptions> getListOfConsumptions() {
        return listOfConsumptions;
    }

    public ArrayList<Equation> getListOfEquation() {
        return listOfEquation;
    }

    public void describeMatrices() {
        System.out.println();
        for (int i = 0; i < coefficientMatrix.length; i++) {
            System.out.print('[');
            for (int j = 0; j < coefficientMatrix[i].length; j++) {
                System.out.print(coefficientMatrix[i][j] + ", ");
            }
            System.out.println(']');
        }
        System.out.println();
        System.out.println();
        System.out.print('[');
        for (int i = 0; i < freeMemoryMatrix.length; i++) {
            System.out.print(freeMemoryMatrix[i] + ", ");
        }
        System.out.println(']');
    }
}

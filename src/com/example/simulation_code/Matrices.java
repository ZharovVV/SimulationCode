package com.example.simulation_code;

import com.example.simulation_code.Elements.*;
import com.example.simulation_code.HelperСlassesAndInterfaces.Consumptions;
import com.example.simulation_code.HelperСlassesAndInterfaces.Equation;
import org.apache.commons.math3.linear.*;

import java.util.ArrayList;


public class Matrices {
    public double[][] coefficientMatrix;
    public double[] freeMemoryMatrix;
    private ArrayList<Consumptions> listOfColumnsOfConsumptions;
    private ArrayList<Equation> listOfLinesOfEquations;

    @SuppressWarnings("ConstantConditions")
    public Matrices(ArrayList<Vertex> vertexList) {
        listOfColumnsOfConsumptions = new ArrayList<>();
        listOfLinesOfEquations = new ArrayList<>();
        int i = 0;
        int j = 0;

        for (Vertex vertex : vertexList) {
            Elements element = vertex.element;
            if (element.getClass() == Heaters.class) {
                Heaters heater = (Heaters) element;
                if (heater.isSurfaceHeater()) {
                    i = i + 3;
                    j = j + 3;
                    listOfColumnsOfConsumptions.add(heater.getConsumptionOfHeatingSteam());
                    listOfColumnsOfConsumptions.add(heater.getConsumptionOfSteamDrain());
                    listOfColumnsOfConsumptions.add(heater.getConsumptionOfHeatedMedium());
                    listOfLinesOfEquations.add(heater.getMaterialBalanceEquationOnSteamDrainLine());
                    listOfLinesOfEquations.add(heater.getMaterialBalanceEquationOnHeatedMediumLine());
                    listOfLinesOfEquations.add(heater.getHeatBalanceEquation());
                } else {
                    i = i + 2;
                    j = j + 2;
                    listOfColumnsOfConsumptions.add(heater.getConsumptionOfHeatingSteam());
                    listOfColumnsOfConsumptions.add(heater.getConsumptionOfHeatedMedium());
                    listOfLinesOfEquations.add(heater.getMaterialBalanceEquationOnHeatedMediumLine());
                    listOfLinesOfEquations.add(heater.getHeatBalanceEquation());
                }
            }


            if (element.getClass() == Superheaters.class) {
                Superheaters superheater = (Superheaters) element;
                i = i + 3;
                j = j + 3;
                listOfColumnsOfConsumptions.add(superheater.getConsumptionOfHeatingSteam());
                listOfColumnsOfConsumptions.add(superheater.getConsumptionOfSteamDrain());
                listOfColumnsOfConsumptions.add(superheater.getConsumptionOfHeatedMedium());
                listOfLinesOfEquations.add(superheater.getMaterialBalanceEquationOnSteamDrainLine());
                listOfLinesOfEquations.add(superheater.getMaterialBalanceEquationOnHeatedMediumLine());
                listOfLinesOfEquations.add(superheater.getHeatBalanceEquation());
            }

            if (element.getClass() == Separator.class) {
                Separator separator = (Separator) element;
                i = i + 2;
                j = j + 3;
                listOfColumnsOfConsumptions.add(separator.getConsumptionOfHeatingSteam());
                listOfColumnsOfConsumptions.add(separator.getConsumptionOfSteamDrain());
                listOfColumnsOfConsumptions.add(separator.getConsumptionOfHeatedMedium());
                listOfLinesOfEquations.add(separator.getMaterialBalanceEquation());
                listOfLinesOfEquations.add(separator.getHeatBalanceEquation());

            }

            if (element.getClass() == TurbineCylinders.class) {
                TurbineCylinders turbineCylinder = (TurbineCylinders) element;
                i = i + 1;
                listOfLinesOfEquations.add(turbineCylinder.getMaterialBalanceEquation());

            }

            if (element.getClass() == Condenser.class) {
                Condenser condenser = (Condenser) element;
                i = i + 1;
                j = j + 2;
                listOfColumnsOfConsumptions.add(condenser.getConsumptionOfHeatingSteam());
                listOfColumnsOfConsumptions.add(condenser.getConsumptionOfSteamDrain());
                listOfLinesOfEquations.add(condenser.getMaterialBalanceEquation());
            }

            if (element.getClass() == Pumps.class) {
                Pumps pump = (Pumps) element;
                i = i + 1;
                j = j + 1;
                listOfColumnsOfConsumptions.add(pump.getConsumptionOfWater());
                listOfLinesOfEquations.add(pump.getMaterialBalanceEquation());
            }
        }
        System.out.println("Число уравнений: " + i + " Число неизвестных: " + j);
        coefficientMatrix = new double[i][j];
        freeMemoryMatrix = new double[i];
    }

    public ArrayList<Consumptions> getListOfColumnsOfConsumptions() {
        return listOfColumnsOfConsumptions;
    }

    public ArrayList<Equation> getListOfLinesOfEquations() {
        return listOfLinesOfEquations;
    }

    public void solvingSystemAndSettingConsumption() {
        RealMatrix matrixOfCoefficient = new Array2DRowRealMatrix(this.coefficientMatrix, false);
        RealVector matrixOfFreeMember = new ArrayRealVector(this.freeMemoryMatrix, false);

        DecompositionSolver solver = new LUDecomposition(matrixOfCoefficient).getSolver();
        RealVector solution = solver.solve(matrixOfFreeMember);

        for (Consumptions consumptions : listOfColumnsOfConsumptions) {
            int i = listOfColumnsOfConsumptions.indexOf(consumptions);
            consumptions.consumptionValue = solution.getEntry(i);
        }
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

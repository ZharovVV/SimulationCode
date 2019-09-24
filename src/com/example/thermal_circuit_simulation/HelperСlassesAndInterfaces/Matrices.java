package com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces;

import com.example.thermal_circuit_simulation.Elements.*;
import com.example.thermal_circuit_simulation.Elements.Ejectors.*;
import com.example.thermal_circuit_simulation.Graph.Vertex;
import org.apache.commons.math3.linear.*;
import java.util.ArrayList;
import java.util.List;


public class Matrices {
    public double[][] coefficientMatrix;
    public double[] freeMemoryMatrix;
    private List<Consumptions> listOfColumnsOfConsumptions;
    private List<Equation> listOfLinesOfEquations;

    public Matrices(List<Vertex> vertexList) {
        listOfColumnsOfConsumptions = new ArrayList<>();
        listOfLinesOfEquations = new ArrayList<>();
        int i = 0;
        int j = 0;

        for (Vertex vertex : vertexList) {
            Element element = vertex.element;
            if (element instanceof Heater) {
                Heater heater = (Heater) element;
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

            if (element instanceof Deaerator) {
                Deaerator deaerator = (Deaerator) element;
                i = i + 2;
                j = j + 2;
                listOfColumnsOfConsumptions.add(deaerator.getConsumptionOfHeatingSteam());
                listOfColumnsOfConsumptions.add(deaerator.getConsumptionOfHeatedMedium());
                listOfLinesOfEquations.add(deaerator.getMaterialBalanceEquationOnHeatedMediumLine());
                listOfLinesOfEquations.add(deaerator.getHeatBalanceEquation());
            }


            if (element instanceof Superheater) {
                Superheater superheater = (Superheater) element;
                i = i + 3;
                j = j + 3;
                listOfColumnsOfConsumptions.add(superheater.getConsumptionOfHeatingSteam());
                listOfColumnsOfConsumptions.add(superheater.getConsumptionOfSteamDrain());
                listOfColumnsOfConsumptions.add(superheater.getConsumptionOfHeatedMedium());
                listOfLinesOfEquations.add(superheater.getMaterialBalanceEquationOnSteamDrainLine());
                listOfLinesOfEquations.add(superheater.getMaterialBalanceEquationOnHeatedMediumLine());
                listOfLinesOfEquations.add(superheater.getHeatBalanceEquation());
            }

            if (element instanceof Separator) {
                Separator separator = (Separator) element;
                i = i + 2;
                j = j + 3;
                listOfColumnsOfConsumptions.add(separator.getConsumptionOfHeatingSteam());
                listOfColumnsOfConsumptions.add(separator.getConsumptionOfSteamDrain());
                listOfColumnsOfConsumptions.add(separator.getConsumptionOfHeatedMedium());
                listOfLinesOfEquations.add(separator.getMaterialBalanceEquation());
                listOfLinesOfEquations.add(separator.getHeatBalanceEquation());

            }

            if (element instanceof TurbineCylinder) {
                TurbineCylinder turbineCylinder = (TurbineCylinder) element;
                i = i + 1;
                listOfLinesOfEquations.add(turbineCylinder.getMaterialBalanceEquation());

            }

            if (element instanceof Condenser) {
                Condenser condenser = (Condenser) element;
                i = i + 1;
                j = j + 2;
                listOfColumnsOfConsumptions.add(condenser.getConsumptionOfHeatingSteam());
                listOfColumnsOfConsumptions.add(condenser.getConsumptionOfSteamDrain());
                listOfLinesOfEquations.add(condenser.getMaterialBalanceEquation());
            }

            if (element instanceof Pump) {
                Pump pump = (Pump) element;
                i = i + 1;
                j = j + 1;
                listOfColumnsOfConsumptions.add(pump.getConsumptionOfWater());
                listOfLinesOfEquations.add(pump.getMaterialBalanceEquation());
            }

            if (element instanceof MainEjectorWithCooler) {
                MainEjectorWithCooler mainEjectorWithCooler = (MainEjectorWithCooler) element;
                i = i + 1;
                j = j + 1;
                listOfColumnsOfConsumptions.add(mainEjectorWithCooler.getConsumptionOfWater());
                listOfLinesOfEquations.add(mainEjectorWithCooler.getMaterialBalanceEquation());
            }

            if (element instanceof SealEjectorWithCooler) {
                SealEjectorWithCooler sealEjectorWithCooler = (SealEjectorWithCooler) element;
                i = i + 1;
                j = j + 1;
                listOfColumnsOfConsumptions.add(sealEjectorWithCooler.getConsumptionOfWater());
                listOfLinesOfEquations.add(sealEjectorWithCooler.getMaterialBalanceEquation());
            }

            if (element instanceof MixingPoint) {
                MixingPoint mixingPoint = (MixingPoint) element;
                i = i + 1;
                j = j + 1;
                listOfColumnsOfConsumptions.add(mixingPoint.getConsumptionOfHeatedMedium());
                listOfLinesOfEquations.add(mixingPoint.getMaterialBalanceEquation());
            }
        }
        /*System.out.println("Число уравнений: " + i + " Число неизвестных: " + j);*/
        coefficientMatrix = new double[i][j];
        freeMemoryMatrix = new double[i];
    }

    public List<Consumptions> getListOfColumnsOfConsumptions() {
        return listOfColumnsOfConsumptions;
    }

    public List<Equation> getListOfLinesOfEquations() {
        return listOfLinesOfEquations;
    }

    public void solvingSystemAndSettingConsumption() {
        RealMatrix matrixOfCoefficient = new Array2DRowRealMatrix(this.coefficientMatrix, false);
        RealVector matrixOfFreeMember = new ArrayRealVector(this.freeMemoryMatrix, false);

        DecompositionSolver solver = new LUDecomposition(matrixOfCoefficient).getSolver();
        RealVector solution = solver.solve(matrixOfFreeMember);

        for (int i = 0; i < listOfColumnsOfConsumptions.size(); i++) {
            Consumptions consumptions = listOfColumnsOfConsumptions.get(i);
            consumptions.consumptionValue = solution.getEntry(i);
        }
    }

    public void describeMatrices() {
        System.out.println();
        for (int i = 0; i < coefficientMatrix.length; i++) {
            System.out.print(listOfLinesOfEquations.get(i).getElement().NAME + " : " + '[');
            for (int j = 0; j < coefficientMatrix[i].length; j++) {
                System.out.print(coefficientMatrix[i][j] + ", ");
            }
            System.out.println(']');
        }
        System.out.println();
        System.out.println();
        System.out.print('[');
        for (double memoryMatrix : freeMemoryMatrix) {
            System.out.print(memoryMatrix + ", ");
        }
        System.out.println(']');
    }
}

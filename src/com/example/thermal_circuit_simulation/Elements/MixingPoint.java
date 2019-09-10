package com.example.thermal_circuit_simulation.Elements;

import com.example.thermal_circuit_simulation.Graph.Graph;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.*;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Matrices;
import com.example.thermal_circuit_simulation.Graph.Vertex;

import java.util.ArrayList;
import java.util.Map;

import static com.example.thermal_circuit_simulation.Graph.Graph.*;

public class MixingPoint extends Element {
    private double pressureOfHeatedMedium;
    private double temperatureOfHeatedMedium;
    private double enthalpyOfHeatedMedium;                      // Энтальпия обогреваемой среды на выходе из точки смешения
    private Consumptions consumptionOfHeatedMedium = new Consumptions();                      // Расход обогреваемой среды на выходе из точки смешения
    private Equation materialBalanceEquation = new Equation(this);
    private Heater previousHeaterOnFeedWaterLine;

    public MixingPoint(String name) {
        super(name);
    }

    @Override
    public void calculationOfInitialParameters(int v, Graph theGraph) {
        //--------------------------Инициализация-----------------------------------------------------------------------
        int nVerts = theGraph.getnVerts();
        Map<Integer, int[][]> adjMat = theGraph.getAdjMat();
        ArrayList<Vertex> vertexList = theGraph.getVertexList();
        //--------------------------------Связи с элементами по линии питательной воды----------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(FEED_WATER)[v][j];
            if (relations == 1) {
                Element element = vertexList.get(j).element;
                if (element.getClass() == Heater.class) {
                    Heater previousHeaterOnFeedWaterLine = (Heater) element;
                    this.previousHeaterOnFeedWaterLine = previousHeaterOnFeedWaterLine;
                    pressureOfHeatedMedium = previousHeaterOnFeedWaterLine.getPressureOfHeatedMedium();
                    temperatureOfHeatedMedium = previousHeaterOnFeedWaterLine.getTemperatureOfHeatedMedium();
                    this.enthalpyOfHeatedMedium = previousHeaterOnFeedWaterLine.getEnthalpyOfHeatedMedium() + 5.0;
                    consumptionOfHeatedMedium.consumptionValue = Double.NaN;
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
    }

    public Consumptions getConsumptionOfHeatedMedium() {
        return consumptionOfHeatedMedium;
    }

    public Equation getMaterialBalanceEquation() {
        return materialBalanceEquation;
    }

    public double getPressureOfHeatedMedium() {
        return pressureOfHeatedMedium;
    }

    public double getTemperatureOfHeatedMedium() {
        return temperatureOfHeatedMedium;
    }

    public double getEnthalpyOfHeatedMedium() {
        return enthalpyOfHeatedMedium;
    }

    @Override
    public void matrixCompilation(int v, Matrices matrices, Graph theGraph) {
        //--------------------------Инициализация-----------------------------------------------------------------------
        int nVerts = theGraph.getnVerts();
        Map<Integer, int[][]> adjMat = theGraph.getAdjMat();
        ArrayList<Vertex> vertexList = theGraph.getVertexList();
        double[][] coefficientMatrix = matrices.coefficientMatrix;
        double[] freeMemoryMatrix = matrices.freeMemoryMatrix;
        ArrayList<Consumptions> listOfConsumptions = matrices.getListOfColumnsOfConsumptions();
        // Получение номера строки в матрице, в которую записывается уравнение материального баланса по линии обогреваемой среды для точки смешения
        int materialBalanceEquationOnHeatedMediumLine = matrices.getListOfLinesOfEquations().indexOf(this.getMaterialBalanceEquation());

        //--------------------------------Связи с элементами по линии дренажа греющего пара-----------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(STEAM_DRAIN)[v][j];
            if (relations == 1) {
                Element element = vertexList.get(j).element;

                if (element.getClass() == Pump.class) {
                    Pump pump = (Pump) element;
                    int indexOfListConsumption = listOfConsumptions.indexOf(pump.getConsumptionOfWater());
                    coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                    if (!(Double.isNaN(this.consumptionOfHeatedMedium.consumptionValue))) { // После первой итерации, когда расход уже известен
                        enthalpyOfHeatedMedium = (
                                previousHeaterOnFeedWaterLine.getConsumptionOfHeatedMedium().consumptionValue * previousHeaterOnFeedWaterLine.getEnthalpyOfHeatedMedium() +
                                        pump.getConsumptionOfWater().consumptionValue * pump.getOutletEnthalpy()) / this.consumptionOfHeatedMedium.consumptionValue;

                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии питательной воды----------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(FEED_WATER)[v][j];
            // Номер столбца расхода обогреваемой среды точки смешения
            int mixingPointIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfHeatedMedium());
            if (relations == 1 || relations == -1) {
                Element element = vertexList.get(j).element;

                if (element.getClass() == Heater.class) {
                    if (relations == -1) {
                        coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][mixingPointIndexOfListConsumption] = relations;
                    } else {
                        Heater heater = (Heater) element;
                        int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfHeatedMedium());
                        coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
    }

    @Override
    public void describe() {
        super.describe();
        System.out.println("Энтальпия воды на выходе из точки смешения: " + enthalpyOfHeatedMedium + " ,кДж/кг");
        System.out.println("Расход воды на выходе из точки смешения: " + consumptionOfHeatedMedium.consumptionValue + " ,кг/c");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }
}

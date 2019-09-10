package com.example.thermal_circuit_simulation.Elements;

import com.example.thermal_circuit_simulation.Graph.Graph;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Consumptions;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Equation;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.MatrixCompilation;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Matrices;
import com.example.thermal_circuit_simulation.Graph.Vertex;
import com.hummeling.if97.IF97;

import java.util.ArrayList;
import java.util.Map;

import static com.example.thermal_circuit_simulation.Graph.Graph.*;

public class Condenser extends Element {
    //-----------------------------Характеристики греющего пара---------------------------------------------------------
    private double pressureOfHeatingSteam;                      // Давление греющего пара на входе в конденсатор
    private double temperatureOfHeatingSteam;                   // Температура греющего пара на входе в конденсатор
    private double enthalpyOfHeatingSteam;                      // Энтальпия греющего пара на входе в конденсатор
    private Consumptions consumptionOfHeatingSteam = new Consumptions();
    //-----------------------------Характеристики дренажа пара----------------------------------------------------------
    private double pressureOfSteamDrain;                        // Давление дренажа пара на выходе из конденсатора
    private double temperatureOfSteamDrain;                     // Температура дренажа пара на выходе из конденсатора
    private double enthalpyOfSteamDrain;                        // Энтальпия дренажа пара на выходе из конденсатора
    private Consumptions consumptionOfSteamDrain = new Consumptions();

    private Equation materialBalanceEquation = new Equation(this);

    public Condenser(String name) {
        super(name);
    }

    @Override
    public void calculationOfInitialParameters(int v, Graph theGraph) {
        //--------------------------Инициализация-----------------------------------------------------------------------
        int nVerts = theGraph.getnVerts();
        Map<Integer, int[][]> adjMat = theGraph.getAdjMat();
        ArrayList<Vertex> vertexList = theGraph.getVertexList();
        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии греющего пара-------------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];
            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;

                if (element.getClass() == TurbineCylinder.class) {
                    TurbineCylinder turbineCylinder = (TurbineCylinder) element;
                    this.pressureOfHeatingSteam = turbineCylinder.parametersInSelection(turbineCylinder.NUMBER_OF_SELECTIONS + 1).getPressure();    // Давление в конденсаторе ( = давлению на выходе из цилиндра)
                    this.enthalpyOfHeatingSteam = turbineCylinder.parametersInSelection(turbineCylinder.NUMBER_OF_SELECTIONS + 1).getEnthalpy();    // Энтальпия пара на входе в конденсатор
                    temperatureOfHeatingSteam = waterSteam.saturationTemperatureP(pressureOfHeatingSteam) - 273.15;
                    pressureOfSteamDrain = pressureOfHeatingSteam;
                    temperatureOfSteamDrain = temperatureOfHeatingSteam;
                    enthalpyOfSteamDrain = waterSteam.specificEnthalpySaturatedLiquidP(pressureOfHeatingSteam);
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
    }

    public double getPressureOfSteamDrain() {
        return pressureOfSteamDrain;
    }

    public double getTemperatureOfSteamDrain() {
        return temperatureOfSteamDrain;
    }

    public double getEnthalpyOfSteamDrain() {
        return enthalpyOfSteamDrain;
    }

    public Consumptions getConsumptionOfHeatingSteam() {
        return consumptionOfHeatingSteam;
    }

    public Consumptions getConsumptionOfSteamDrain() {
        return consumptionOfSteamDrain;
    }

    public Equation getMaterialBalanceEquation() {
        return materialBalanceEquation;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void matrixCompilation(int v, Matrices matrices, Graph theGraph) {
        //--------------------------Инициализация-----------------------------------------------------------------------
        int nVerts = theGraph.getnVerts();
        Map<Integer, int[][]> adjMat = theGraph.getAdjMat();
        ArrayList<Vertex> vertexList = theGraph.getVertexList();
        double[][] coefficientMatrix = matrices.coefficientMatrix;
        double[] freeMemoryMatrix = matrices.freeMemoryMatrix;
        ArrayList<Consumptions> listOfConsumptions = matrices.getListOfColumnsOfConsumptions();
        // Получение номера строки в матрице, в которую записывается уравнение материального баланса для Конденсатора
        int materialBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(this.getMaterialBalanceEquation());
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии греющего пара-------------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];
            // Получение номера столбца расхода греющего пара конденсатора
            int condenserIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfHeatingSteam());
            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;

                if (element.getClass() == TurbineCylinder.class) {
                    coefficientMatrix[materialBalanceEquation][condenserIndexOfListConsumption] = relations;
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии дренажа греющего пара-----------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(STEAM_DRAIN)[v][j];
            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;

                if (element.getClass() == Heater.class) {
                    Heater heater = (Heater) element;
                    int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfSteamDrain());
                    coefficientMatrix[materialBalanceEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == TurboDrive.class) {
                    TurboDrive turboDrive = (TurboDrive) element;
                    freeMemoryMatrix[materialBalanceEquation] += (-1) * relations * turboDrive.getSteamConsumption();
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии питательной воды----------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(FEED_WATER)[v][j];
            // Получение номера столбца расхода дренажа греющего пара конденсатора
            int condenserIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfSteamDrain());
            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;

                if (element.getClass() == Pump.class) {
                    coefficientMatrix[materialBalanceEquation][condenserIndexOfListConsumption] = relations;
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------


    }

    @Override
    public void describe() {
        super.describe();
        System.out.println("Параметры на входе в конденсатор:");
        System.out.println("Давление: " + pressureOfHeatingSteam + " ,МПа");
        System.out.println("Температура: " + temperatureOfHeatingSteam + " ,℃");
        System.out.println("Энтальпия: " + enthalpyOfHeatingSteam + " ,кДж/кг");
        System.out.println("Расход греющего пара: " + consumptionOfHeatingSteam.consumptionValue + " ,кг/c");
        System.out.println();
        System.out.println("Параметры на выходе из конденсатора:");
        System.out.println("Давление: " + pressureOfSteamDrain + " ,МПа");
        System.out.println("Температура: " + temperatureOfSteamDrain + " ,℃");
        System.out.println("Энтальпия: " + enthalpyOfSteamDrain + " ,кДж/кг");
        System.out.println("Расход воды на выходе из конденсатора: " + consumptionOfSteamDrain.consumptionValue + " ,кг/c");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }
}

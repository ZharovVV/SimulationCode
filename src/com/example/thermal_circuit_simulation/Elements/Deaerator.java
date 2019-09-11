package com.example.thermal_circuit_simulation.Elements;

import com.example.thermal_circuit_simulation.Graph.Graph;
import com.example.thermal_circuit_simulation.Graph.Vertex;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.*;
import com.hummeling.if97.IF97;

import java.util.ArrayList;
import java.util.Map;

import static com.example.thermal_circuit_simulation.Graph.Graph.*;

public class Deaerator extends Element {
    //-----------------------------Характеристики подогревателя---------------------------------------------------------
    private int selectionNumber;                                // Номер отбора
    //-----------------------------Характеристики греющего пара---------------------------------------------------------
    private double pressureOfHeatingSteam;                      // Давление греющего пара на входе в подогреватель
    private double temperatureOfHeatingSteam;                   // Температура греющего пара на входе в подогреватель
    private double enthalpyOfHeatingSteam;                      // Энтальпия греющего пара на входе в подогреватель
    private Consumptions consumptionOfHeatingSteam = new Consumptions();                      // Расход греющего пара на входе в подогреватель
    //-----------------------------Характеристики обогреваемой среды на выходе------------------------------------------
    private double pressureOfHeatedMedium;                      // Давление обогреваемой среды на выходе из подогревателя
    private double temperatureOfHeatedMedium;                   // Температура обогреваемой среды на выходе из подогревателя
    private double enthalpyOfHeatedMedium;                      // Энтальпия обогреваемой среды на выходе из подогревателя
    private Consumptions consumptionOfHeatedMedium = new Consumptions();                      // Расход обогреваемой среды на выходе из подогревателя
    private Equation materialBalanceEquationOnHeatedMediumLine = new Equation(this);
    private Equation heatBalanceEquation = new Equation(this);

    public Deaerator(String name, double pressureInHeater) {
        super(name);
        this.pressureOfHeatingSteam = pressureInHeater;
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

                if (element instanceof TurbineCylinder && relations == 1) {
                    TurbineCylinder turbineCylinder = (TurbineCylinder) element;
                    this.temperatureOfHeatingSteam = waterSteam.saturationTemperatureP(pressureOfHeatingSteam) - 273.15;
                    this.enthalpyOfHeatingSteam = turbineCylinder.parametersInSelection(selectionNumber).getEnthalpy();
                    this.pressureOfHeatedMedium = pressureOfHeatingSteam;
                    this.temperatureOfHeatedMedium = temperatureOfHeatingSteam;
                    this.enthalpyOfHeatedMedium = waterSteam.specificEnthalpySaturatedLiquidP(pressureOfHeatingSteam);
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
    }

    @Override
    public void setSelectionNumber(int selectionNumber) {
        this.selectionNumber = selectionNumber;
    }

    public double getPressureOfHeatedMedium() {
        return pressureOfHeatedMedium;
    }

    public double getTemperatureOfHeatedMedium() {
        return temperatureOfHeatedMedium;
    }

    public double getEnthalpyOfHeatingSteam() {
        return enthalpyOfHeatingSteam;
    }

    public Consumptions getConsumptionOfHeatingSteam() {
        return consumptionOfHeatingSteam;
    }

    public double getEnthalpyOfHeatedMedium() {
        return enthalpyOfHeatedMedium;
    }

    public Consumptions getConsumptionOfHeatedMedium() {
        return consumptionOfHeatedMedium;
    }

    public Equation getMaterialBalanceEquationOnHeatedMediumLine() {
        return materialBalanceEquationOnHeatedMediumLine;
    }

    public Equation getHeatBalanceEquation() {
        return heatBalanceEquation;
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
        // Получение номера строки в матрице, в которую записывается уравнение материального баланса по линии обогреваемой среды для Подогревателя
        int materialBalanceEquationOnHeatedMediumLine = matrices.getListOfLinesOfEquations().indexOf(this.getMaterialBalanceEquationOnHeatedMediumLine());
        // Получение номера строки в матрице, в которую записывается уравнение теплового баланса для Подогревателя
        int heatBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(this.getHeatBalanceEquation());
        //----------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии греющего пара---------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];
            // Номер столбца расхода греющего пара Подогревателя
            int heaterIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfHeatingSteam());
            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;

                if (element instanceof TurbineCylinder && relations == 1) {
                    coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][heaterIndexOfListConsumption] = relations;
                    coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfHeatingSteam();
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии дренажа греющего пара-----------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(STEAM_DRAIN)[v][j];
            if (relations == 1) {
                Element element = vertexList.get(j).element;

                if (element instanceof Separator) {
                    Separator separator = (Separator) element;
                    // Номер столбца расхода дренажа греющего пара Сепаратора
                    int indexOfListConsumption = listOfConsumptions.indexOf(separator.getConsumptionOfSteamDrain());
                    coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                    coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * separator.getEnthalpyOfSteamDrain();
                }

                if (element instanceof Superheater) {
                    Superheater superheater = (Superheater) element;
                    // Номер столбца расхода дренажа греющего пара Пароперегревателя
                    int indexOfListConsumption = listOfConsumptions.indexOf(superheater.getConsumptionOfSteamDrain());
                    coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                    coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * superheater.getEnthalpyOfSteamDrain();
                }

                if (element instanceof Heater) {
                    Heater heater = (Heater) element;
                    // Номер столбца расхода дренажа греющего пара Подогревателя
                    int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfSteamDrain());
                    coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                    coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * heater.getEnthalpyOfSteamDrain();
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии питательной воды----------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(FEED_WATER)[v][j];
            // Номер столбца расхода обогреваемой среды Подогревателя
            int heaterIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfHeatedMedium());
            if (relations == 1 || relations == -1) {
                Element element = vertexList.get(j).element;

                if (element instanceof Pump) {
                    if (relations == -1) {
                        coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][heaterIndexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfHeatedMedium();
                    } else {
                        Pump pump = (Pump) element;
                        // Номер столбца расхода воды Насоса
                        int indexOfListConsumption = listOfConsumptions.indexOf(pump.getConsumptionOfWater());
                        coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * pump.getOutletEnthalpy();
                    }
                }

                if (element instanceof Heater) {
                    if (relations == -1) {
                        coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][heaterIndexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfHeatedMedium();
                    } else {
                        Heater heater = (Heater) element;
                        // Номер столбца расхода обогреваемой среды Подогревателя
                        int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfHeatedMedium());
                        coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * heater.getEnthalpyOfHeatedMedium();
                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
    }

    @Override
    public void describe() {
        super.describe();
        System.out.println("-----------------------------Характеристики греющего пара---------------------------------------------------------");
        System.out.println("Давление греющего пара на входе в подогреватель: " + pressureOfHeatingSteam + " ,МПа");
        System.out.println("Температура греющего пара на входе в подогреватель: " + temperatureOfHeatingSteam + " ,℃");
        System.out.println("Энтальпия греющего пара на входе в подогреватель: " + enthalpyOfHeatingSteam + " ,кДж/кг");
        System.out.println("Расход греющего пара: " + consumptionOfHeatingSteam.consumptionValue + " ,кг/c");
        System.out.println("-----------------------------Характеристики обогреваемой среды на выходе------------------------------------------");
        System.out.println("Давление обогреваемой среды на выходе из подогревателя: " + pressureOfHeatedMedium + " ,МПа");
        System.out.println("Температура обогреваемой среды на выходе из подогревателя: " + temperatureOfHeatedMedium + " ,℃");
        System.out.println("Энтальпия обогреваемой среды на выходе из подогревателя: " + enthalpyOfHeatedMedium + " ,кДж/кг");
        System.out.println("Расход обогреваемой среды на выходе из подогревателя: " + consumptionOfHeatedMedium.consumptionValue + " ,кг/c");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }
}

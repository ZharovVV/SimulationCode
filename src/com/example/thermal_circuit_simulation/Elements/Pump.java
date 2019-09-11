package com.example.thermal_circuit_simulation.Elements;

import com.example.thermal_circuit_simulation.Elements.Ejectors.MainEjectorWithCooler;
import com.example.thermal_circuit_simulation.Elements.Ejectors.SealEjectorWithCooler;
import com.example.thermal_circuit_simulation.Graph.Graph;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.*;
import com.example.thermal_circuit_simulation.Graph.Vertex;
import com.example.thermal_circuit_simulation.ThermalEfficiencyIndicators.ThermalEfficiencyIndicators;
import com.hummeling.if97.IF97;

import java.util.ArrayList;
import java.util.Map;

import static com.example.thermal_circuit_simulation.Graph.Graph.*;

public class Pump extends Element {
    private double efficiency;              // КПД насоса
    private double pumpHead;                // Необходимый напор насоса
    private double pumpDriveEfficiency;     // КПД привода насоса (Если используется турбопривод, то имеется в виду механический КПД
    private double inletTemperature;        // Температура на входе в насос
    private double inletPressure;           // Давление на входе в насос
    private double inletEnthalpy;           // Энтальпия на входе в насос
    private double enthalpyIncrease;        // Повышение энтальпии в насосе
    private double outletTemperature;       // Температура на выходе из насоса
    private double outletPressure;          // Давление на выходе из насоса
    private double outletEnthalpy;          // Энтальпия на выходе из насоса
    private boolean isThePumpDriveElectric; // Привод насоса электрический ?

    private Consumptions consumptionOfWater = new Consumptions();
    private Equation materialBalanceEquation = new Equation(this);

    public Pump(String name, double efficiency, double pumpHead, boolean isThePumpDriveElectric, double pumpDriveEfficiency) {
        super(name);
        this.efficiency = efficiency;
        this.pumpDriveEfficiency = pumpDriveEfficiency;
        this.pumpHead = pumpHead;
        this.isThePumpDriveElectric = isThePumpDriveElectric;
    }

    @Override
    public void calculationOfInitialParameters(int v, Graph theGraph) {
        //--------------------------Инициализация-----------------------------------------------------------------------
        int nVerts = theGraph.getnVerts();
        Map<Integer, int[][]> adjMat = theGraph.getAdjMat();
        ArrayList<Vertex> vertexList = theGraph.getVertexList();
        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии питательной воды----------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(FEED_WATER)[v][j];
            if (relations == 1) {
                Element element = vertexList.get(j).element;

                if (element instanceof Condenser) {
                    Condenser condenser = (Condenser) element;
                    this.inletTemperature = condenser.getTemperatureOfSteamDrain();
                    this.inletPressure = condenser.getPressureOfSteamDrain();
                    this.inletEnthalpy = condenser.getEnthalpyOfSteamDrain();
                }

                if (element instanceof Heater) {
                    Heater heater = (Heater) element;
                    this.inletTemperature = heater.getTemperatureOfHeatedMedium();
                    this.inletPressure = heater.getPressureOfHeatedMedium();
                    this.inletEnthalpy = heater.getEnthalpyOfHeatedMedium();
                }

                if (element instanceof Deaerator) {
                    Deaerator deaerator = (Deaerator) element;
                    this.inletTemperature = deaerator.getTemperatureOfHeatedMedium();
                    this.inletPressure = deaerator.getPressureOfHeatedMedium();
                    this.inletEnthalpy = deaerator.getEnthalpyOfHeatedMedium();
                }

                if (element instanceof SealEjectorWithCooler) {
                    SealEjectorWithCooler sealEjectorWithCooler = (SealEjectorWithCooler) element;
                    this.inletTemperature = sealEjectorWithCooler.getOutletTemperature();
                    this.inletPressure = sealEjectorWithCooler.getOutletPressure();
                    this.inletEnthalpy = sealEjectorWithCooler.getOutletEnthalpy();
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //---------------------------------Связи с элементами по линии дренажа греющего пара----------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(STEAM_DRAIN)[v][j];
            if (relations == 1) {
                Element element = vertexList.get(j).element;

                if (element instanceof Heater) {
                    Heater heater = (Heater) element;
                    this.inletTemperature = heater.getTemperatureOfSteamDrain();
                    this.inletPressure = heater.getPressureOfSteamDrain();
                    this.inletEnthalpy = heater.getEnthalpyOfSteamDrain();
                }

            }
        }
        //--------------------------------------------------------------------------------------------------------------

        this.outletTemperature = inletTemperature;
        this.outletPressure = inletPressure + pumpHead;
        this.enthalpyIncrease =
                pumpHead * waterSteam.specificVolumePT((inletPressure + outletPressure) / 2, inletTemperature + 273.15) * 1000 / efficiency;
        this.outletEnthalpy = inletEnthalpy + enthalpyIncrease;
    }

    public double getOutletTemperature() {
        return outletTemperature;
    }

    public double getOutletPressure() {
        return outletPressure;
    }

    public double getEnthalpyIncrease() {
        return enthalpyIncrease;
    }

    public double getOutletEnthalpy() {
        return outletEnthalpy;
    }

    public double getPumpDriveEfficiency() {
        return pumpDriveEfficiency;
    }

    public Consumptions getConsumptionOfWater() {
        return consumptionOfWater;
    }

    public Equation getMaterialBalanceEquation() {
        return materialBalanceEquation;
    }

    @Override
    public void describe() {
        super.describe();
        System.out.println("КПД насоса: " + efficiency);
        System.out.println("Напор насоса: " + pumpHead + " ,МПа");
        System.out.println("Повышение энтальпии в насосе: " + enthalpyIncrease + " ,кДж/кг");
        System.out.println("Параметры на входе в насос:");
        System.out.println("Давление: " + inletPressure + " ,МПа");
        System.out.println("Температура: " + inletTemperature + " ,℃");
        System.out.println("Энтальпия: " + inletEnthalpy + " ,кДж/кг");
        System.out.println();
        System.out.println("Параметры на выходе из насоса:");
        System.out.println("Давление: " + outletPressure + " ,МПа");
        System.out.println("Температура: " + outletTemperature + " ,℃");
        System.out.println("Энтальпия: " + outletEnthalpy + " ,кДж/кг");
        System.out.println("Расход воды: " + consumptionOfWater.consumptionValue + " ,кг/c");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
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
        // Получение номера строки в матрице, в которую записывается уравнение материального баланса для Насоса
        int materialBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(this.getMaterialBalanceEquation());
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии питательной воды----------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(FEED_WATER)[v][j];
            // Получение номера столбца расхода воды в насосе
            int pumpIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfWater());
            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;

                if (element instanceof Condenser) {
                    Condenser condenser = (Condenser) element;
                    // Получение номера столбца расхода дренажа пара конденсатора
                    int indexOfListConsumption = listOfConsumptions.indexOf(condenser.getConsumptionOfSteamDrain());
                    coefficientMatrix[materialBalanceEquation][indexOfListConsumption] = relations;
                }

                if (element instanceof Heater) {
                    if (relations == -1) {
                        coefficientMatrix[materialBalanceEquation][pumpIndexOfListConsumption] = relations;
                    } else {
                        Heater heater = (Heater) element;
                        // Получение номера столбца расхода обогреваемой среды подогревателя
                        int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfHeatedMedium());
                        coefficientMatrix[materialBalanceEquation][indexOfListConsumption] = relations;
                    }
                }

                if (element instanceof Deaerator) {
                    if (relations == -1) {
                        coefficientMatrix[materialBalanceEquation][pumpIndexOfListConsumption] = relations;
                    } else {
                        Deaerator deaerator = (Deaerator) element;
                        // Получение номера столбца расхода обогреваемой среды подогревателя
                        int indexOfListConsumption = listOfConsumptions.indexOf(deaerator.getConsumptionOfHeatedMedium());
                        coefficientMatrix[materialBalanceEquation][indexOfListConsumption] = relations;
                    }
                }

                if (element instanceof MainEjectorWithCooler && relations == -1) {
                    coefficientMatrix[materialBalanceEquation][pumpIndexOfListConsumption] = relations;
                }

                if (element instanceof SealEjectorWithCooler && relations == 1) {
                    SealEjectorWithCooler ejector = (SealEjectorWithCooler) element;
                    // Получение номера столбца расхода воды охладителя
                    int indexOfListConsumption = listOfConsumptions.indexOf(ejector.getConsumptionOfWater());
                    coefficientMatrix[materialBalanceEquation][indexOfListConsumption] = relations;
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //---------------------------------Связи с элементами по линии дренажа греющего пара----------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(STEAM_DRAIN)[v][j];
            // Получение номера столбца расхода воды в насосе
            int pumpIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfWater());
            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;

                if (element instanceof Heater) {
                    if (relations == -1) {
                        coefficientMatrix[materialBalanceEquation][pumpIndexOfListConsumption] = relations;
                    } else {
                        Heater heater = (Heater) element;
                        // Получение номера столбца расхода дренажа греющего пара подогревателя
                        int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfSteamDrain());
                        coefficientMatrix[materialBalanceEquation][indexOfListConsumption] = relations;
                    }
                }

                if (element instanceof MixingPoint) {
                    if (relations == -1) {
                        coefficientMatrix[materialBalanceEquation][pumpIndexOfListConsumption] = relations;
                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
    }

    @Override
    public void calculationOfThermalEfficiencyIndicators(int v, ThermalEfficiencyIndicators thermalEfficiencyIndicators, Graph theGraph) {
        if (isThePumpDriveElectric) {
            thermalEfficiencyIndicators.getMapOfPowerConsumptionForPumpDrive().put(this, enthalpyIncrease * consumptionOfWater.consumptionValue / pumpDriveEfficiency / 1000);
        }
    }
}

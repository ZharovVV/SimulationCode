package com.example.simulation_code.Elements;

import com.example.simulation_code.Graph;
import com.example.simulation_code.HelperСlassesAndInterfaces.Consumptions;
import com.example.simulation_code.HelperСlassesAndInterfaces.Equation;
import com.example.simulation_code.HelperСlassesAndInterfaces.MatrixCompilation;
import com.example.simulation_code.Matrices;
import com.example.simulation_code.Vertex;
import com.hummeling.if97.IF97;

import java.util.ArrayList;
import java.util.Map;

import static com.example.simulation_code.Graph.*;


public class Separator extends Elements implements MatrixCompilation {
    //------------------------------Характеристики сепаратора-----------------------------------------------------------
    private double hydraulicResistanceFromCylinderToSeparator;    // Гидравлическое сопротивление от отбора до сепаратора
    private double outletDegreeOfDryness;                         // Степень сухости на выходе из сепаратора
    //-----------------------------Характеристики греющего пара---------------------------------------------------------
    private double pressureOfHeatingSteam;                      // Давление греющего пара на входе в сепаратор
    private double temperatureOfHeatingSteam;                   // Температура греющего пара на входе в сепаратор
    private double enthalpyOfHeatingSteam;                      // Энтальпия греющего пара на входе в сепаратор
    private Consumptions consumptionOfHeatingSteam = new Consumptions();
    //-----------------------------Характеристики дренажа пара----------------------------------------------------------
    private double pressureOfSteamDrain;                        // Давление дренажа пара на выходе из сепаратора
    private double temperatureOfSteamDrain;                     // Температура дренажа пара на выходе из сепаратора
    private double enthalpyOfSteamDrain;                        // Энтальпия дренажа пара на выходе из сепаратора
    private Consumptions consumptionOfSteamDrain = new Consumptions();                       // Расход дренажа пара на выходе из сепаратора
    //-----------------------------Характеристики обогреваемой среды на выходе------------------------------------------
    private double pressureOfHeatedMedium;                      // Давление сепарируемой среды на выходе из сепаратора
    private double temperatureOfHeatedMedium;                   // Температура сепарируемой среды на выходе из сепаратора
    private double enthalpyOfHeatedMedium;                      // Энтальпия сепарируемой среды на выходе из сепаратора
    private Consumptions consumptionOfHeatedMedium = new Consumptions();

    private Equation materialBalanceEquation = new Equation(this);
    private Equation heatBalanceEquation = new Equation(this);

    public Separator(String name, double hydraulicResistanceFromCylinderToSeparator, double outletDegreeOfDryness, TurbineCylinders turbineCylinder) {
        super(name);
        this.hydraulicResistanceFromCylinderToSeparator = hydraulicResistanceFromCylinderToSeparator;
        this.outletDegreeOfDryness = outletDegreeOfDryness;
        this.pressureOfHeatingSteam =
                turbineCylinder.parametersInSelection(turbineCylinder.NUMBER_OF_SELECTIONS + 1).getPressure() - hydraulicResistanceFromCylinderToSeparator;
        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
        this.temperatureOfHeatingSteam = waterSteam.saturationTemperatureP(pressureOfHeatingSteam) - 273.15;
        this.enthalpyOfHeatingSteam = turbineCylinder.parametersInSelection(turbineCylinder.NUMBER_OF_SELECTIONS + 1).getEnthalpy();
        this.pressureOfSteamDrain = pressureOfHeatingSteam;
        this.temperatureOfSteamDrain = temperatureOfHeatingSteam;
        this.enthalpyOfSteamDrain = waterSteam.specificEnthalpySaturatedLiquidP(pressureOfSteamDrain);
        this.pressureOfHeatedMedium = pressureOfSteamDrain;
        this.temperatureOfHeatedMedium = temperatureOfHeatingSteam;
        this.enthalpyOfHeatedMedium = waterSteam.specificEnthalpyPX(pressureOfHeatingSteam,outletDegreeOfDryness);
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

    public double getEnthalpyOfSteamDrain() {
        return enthalpyOfSteamDrain;
    }

    public double getEnthalpyOfHeatedMedium() {
        return enthalpyOfHeatedMedium;
    }

    public Consumptions getConsumptionOfHeatingSteam() {
        return consumptionOfHeatingSteam;
    }

    public Consumptions getConsumptionOfSteamDrain() {
        return consumptionOfSteamDrain;
    }

    public Consumptions getConsumptionOfHeatedMedium() {
        return consumptionOfHeatedMedium;
    }

    public Equation getMaterialBalanceEquation() {
        return materialBalanceEquation;
    }

    public Equation getHeatBalanceEquation() {
        return heatBalanceEquation;
    }

    @Override
    public void describe() {
        super.describe();
        System.out.println("-----------------------------Характеристики подогревателя---------------------------------------------------------");
        System.out.println("Гидравлическое сопротивление от отбора до сепаратора: " + hydraulicResistanceFromCylinderToSeparator + " ,МПа");
        System.out.println("Степень сухости на выходе из сепаратора: " + outletDegreeOfDryness);
        System.out.println("-----------------------------Характеристики греющего пара---------------------------------------------------------");
        System.out.println("Давление греющего пара на входе в подогреватель: " + pressureOfHeatingSteam + " ,МПа");
        System.out.println("Температура греющего пара на входе в подогреватель: " + temperatureOfHeatingSteam + " ,℃");
        System.out.println("Энтальпия греющего пара на входе в подогреватель: " + enthalpyOfHeatingSteam + " ,кДж/кг");
        System.out.println("Расход греющего пара: " + consumptionOfHeatingSteam.consumptionValue + " ,кг/c");
        System.out.println("-----------------------------Характеристики дренажа пара----------------------------------------------------------");
        System.out.println("Давление дренажа пара на выходе из подогревателя: " + pressureOfSteamDrain + " ,МПа");
        System.out.println("Температура дренажа пара на выходе из подогревателя: " + temperatureOfSteamDrain + " ,℃");
        System.out.println("Энтальпия дренажа пара на выходе из подогревателя: " + enthalpyOfSteamDrain + " ,кДж/кг");
        System.out.println("Расход дренажа пара на выходе из подогревателя: " + consumptionOfSteamDrain.consumptionValue + " ,кг/c");
        System.out.println("-----------------------------Характеристики обогреваемой среды на выходе------------------------------------------");
        System.out.println("Давление обогреваемой среды на выходе из подогревателя: " + pressureOfHeatedMedium + " ,МПа");
        System.out.println("Температура обогреваемой среды на выходе из подогревателя: " + temperatureOfHeatedMedium + " ,℃");
        System.out.println("Энтальпия обогреваемой среды на выходе из подогревателя: " + enthalpyOfHeatedMedium + " ,кДж/кг");
        System.out.println("Расход обогреваемой среды на выходе из подогревателя: " + consumptionOfHeatedMedium.consumptionValue + " ,кг/c");
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

        // Получение номера строки в матрице, в которую записывается уравнение материального баланса Сепаратора
        int materialBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(this.getMaterialBalanceEquation());
        // Получение номера строки в матрице, в которую записывается уравнение теплового баланса Сепаратора
        int heatBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(this.getHeatBalanceEquation());
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии перегретого пара----------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(SUPERHEATED_STEAM)[v][j];
            int separatorIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfHeatedMedium());
            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;

                if (element.getClass() == Superheaters.class) {
                    coefficientMatrix[materialBalanceEquation][separatorIndexOfListConsumption] = relations;
                    coefficientMatrix[heatBalanceEquation][separatorIndexOfListConsumption] = relations * this.getEnthalpyOfHeatedMedium();
                }

                if (element.getClass() == TurbineCylinders.class) {
                    coefficientMatrix[materialBalanceEquation][separatorIndexOfListConsumption] = relations;
                    if (relations == 1) {
                        coefficientMatrix[heatBalanceEquation][separatorIndexOfListConsumption] = relations * this.getEnthalpyOfHeatingSteam();
                    } else {
                        coefficientMatrix[heatBalanceEquation][separatorIndexOfListConsumption] = relations * this.getEnthalpyOfHeatedMedium();
                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии греющего пара-------------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];
            int separatorIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfHeatingSteam());
            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;
                if (element.getClass() == TurbineCylinders.class) {
                    coefficientMatrix[materialBalanceEquation][separatorIndexOfListConsumption] = relations;
                    if (relations == 1) {
                        coefficientMatrix[heatBalanceEquation][separatorIndexOfListConsumption] = relations * this.getEnthalpyOfHeatingSteam();
                    } else {
                        coefficientMatrix[heatBalanceEquation][separatorIndexOfListConsumption] = relations * this.getEnthalpyOfHeatedMedium();
                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии дренажа греющего пара-----------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(STEAM_DRAIN)[v][j];
            int separatorIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfSteamDrain());

            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;

                if (element.getClass() == Heaters.class) {
                    coefficientMatrix[materialBalanceEquation][separatorIndexOfListConsumption] = relations;
                    coefficientMatrix[heatBalanceEquation][separatorIndexOfListConsumption] = relations * this.getEnthalpyOfSteamDrain();
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
    }
}

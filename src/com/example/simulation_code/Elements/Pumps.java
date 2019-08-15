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

import static com.example.simulation_code.Graph.FEED_WATER;

public class Pumps extends Elements implements MatrixCompilation {

    private double efficiency;              // КПД насоса
    private double pumpHead;                // Необходимый напор насоса

    private double inletTemperature;        // Температура на входе в насос
    private double inletPressure;           // Давление на входе в насос
    private double inletEnthalpy;           // Энтальпия на входе в насос
    private double enthalpyIncrease;        // Повышение энтальпии в насосе
    private double outletTemperature;       // Температура на выходе из насоса
    private double outletPressure;          // Давление на выходе из насоса
    private double outletEnthalpy;          // Энтальпия на выходе из насоса

    private Consumptions consumptionOfWater = new Consumptions();
    private Equation materialBalanceEquation = new Equation(this);

    public Pumps(String name, double efficiency, double pumpHead, Elements previousElement) {
        super(name);
        this.efficiency = efficiency;
        this.pumpHead = pumpHead;
        if (previousElement.getClass() == Heaters.class) {                          // Если предыдущий элемент - подогреватель
            Heaters previousHeater = (Heaters) previousElement;
            this.inletTemperature = previousHeater.getTemperatureOfHeatedMedium();
            this.inletPressure = previousHeater.getPressureOfHeatedMedium();
            this.inletEnthalpy = previousHeater.getEnthalpyOfHeatedMedium();
        } else {                                                                    // Если предыдущий элемент - конденсатор
            Condenser condenser = (Condenser) previousElement;
            this.inletTemperature = condenser.getTemperatureOfSteamDrain();
            this.inletPressure = condenser.getPressureOfSteamDrain();
            this.inletEnthalpy = condenser.getEnthalpyOfSteamDrain();
        }
        this.outletTemperature = inletTemperature;
        this.outletPressure = inletPressure + pumpHead;

        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);

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
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println();
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
        // Получение номера строки в матрице, в которую записывается уравнение материального баланса для Насоса
        int materialBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(this.getMaterialBalanceEquation());
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии питательной воды----------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(FEED_WATER)[v][j];
            // Получение номера столбца расхода воды в насосе
            int pumpIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfWater());
            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;

                if (element.getClass() == Condenser.class) {
                    Condenser condenser = (Condenser) element;
                    // Получение номера столбца расхода дренажа пара конденсатора
                    int indexOfListConsumption = listOfConsumptions.indexOf(condenser.getConsumptionOfSteamDrain());
                    coefficientMatrix[materialBalanceEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == Heaters.class) {
                    if (relations == -1) {
                        coefficientMatrix[materialBalanceEquation][pumpIndexOfListConsumption] = relations;
                    } else {
                        Heaters heater = (Heaters) element;
                        // Получение номера столбца расхода обогреваемой среды подогревателя
                        int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfHeatedMedium());
                        coefficientMatrix[materialBalanceEquation][indexOfListConsumption] = relations;
                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
    }
}

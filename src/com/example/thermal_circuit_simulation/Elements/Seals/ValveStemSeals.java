package com.example.thermal_circuit_simulation.Elements.Seals;

import com.example.thermal_circuit_simulation.Elements.*;
import com.example.thermal_circuit_simulation.Graph.Graph;
import com.example.thermal_circuit_simulation.Graph.Vertex;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Consumptions;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Matrices;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.MatrixCompilation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.thermal_circuit_simulation.Graph.Graph.*;

/**
 * Уплотнения штоков клапанов турбины
 */

public class ValveStemSeals  extends Elements implements MatrixCompilation {
    // TODO: 26.08.2019 Костыль, можно сделать лучше...
    private HashMap<Elements,Double> elementContributionToSteamConsumptionInSeals;
    private TurbineCylinders turbineCylinder;


    public ValveStemSeals(String name, HashMap<Elements,Double> elementContributionToSteamConsumptionInSeals, TurbineCylinders turbineCylinder) {
        super(name);
        this.elementContributionToSteamConsumptionInSeals = elementContributionToSteamConsumptionInSeals;
        this.turbineCylinder = turbineCylinder;
    }

    public HashMap<Elements, Double> getElementContributionToSteamConsumptionInSeals() {
        return elementContributionToSteamConsumptionInSeals;
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

        //--------------------------------Связи с элементами по линии греющего пара-------------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];
            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;

                if (element.getClass() == TurbineCylinders.class) {
                    TurbineCylinders turbineCylinder = (TurbineCylinders) element;
                    int materialBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(turbineCylinder.getMaterialBalanceEquation());
                    freeMemoryMatrix[materialBalanceEquation] += relations * elementContributionToSteamConsumptionInSeals.get(turbineCylinder);
                }

                if (element.getClass() == Separator.class) {
                    Separator separator = (Separator) element;
                    int materialBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(separator.getMaterialBalanceEquation());
                    int heatBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(separator.getHeatBalanceEquation());
                    freeMemoryMatrix[materialBalanceEquation] += relations * elementContributionToSteamConsumptionInSeals.get(separator);
                    freeMemoryMatrix[heatBalanceEquation] +=
                            relations * elementContributionToSteamConsumptionInSeals.get(separator) * turbineCylinder.parametersInSelection(0).getEnthalpy();

                }

                if (element.getClass() == Heaters.class) {
                    Heaters heater = (Heaters) element;
                    if (heater.isSurfaceHeater()) {
                        int materialBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(heater.getMaterialBalanceEquationOnSteamDrainLine());
                        int heatBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(heater.getHeatBalanceEquation());
                        freeMemoryMatrix[materialBalanceEquation] += relations * elementContributionToSteamConsumptionInSeals.get(heater);
                        freeMemoryMatrix[heatBalanceEquation] +=
                                relations * elementContributionToSteamConsumptionInSeals.get(heater) * turbineCylinder.parametersInSelection(0).getEnthalpy();
                    } else {
                        int materialBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(heater.getMaterialBalanceEquationOnHeatedMediumLine());
                        int heatBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(heater.getHeatBalanceEquation());
                        freeMemoryMatrix[materialBalanceEquation] += relations * elementContributionToSteamConsumptionInSeals.get(heater);
                        freeMemoryMatrix[heatBalanceEquation] +=
                                relations * elementContributionToSteamConsumptionInSeals.get(heater) * turbineCylinder.parametersInSelection(0).getEnthalpy();
                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

    }

    @Override
    public void describe() {
        super.describe();
        for (Map.Entry<Elements, Double> elementsDoubleEntry : elementContributionToSteamConsumptionInSeals.entrySet()) {
            System.out.println("Элемент схемы: " + elementsDoubleEntry.getKey().NAME +
                    " Расход пара из (в) уплотнения: " + elementContributionToSteamConsumptionInSeals.get(elementsDoubleEntry.getKey()));
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }

}

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

public class ValveStemSeals  extends Element implements MatrixCompilation {
    // TODO: 26.08.2019 Костыль, можно сделать лучше...
    private HashMap<Element,Double> elementContributionToSteamConsumptionInSeals;
    private TurbineCylinder turbineCylinder;


    public ValveStemSeals(String name, HashMap<Element,Double> elementContributionToSteamConsumptionInSeals, TurbineCylinder turbineCylinder) {
        super(name);
        this.elementContributionToSteamConsumptionInSeals = elementContributionToSteamConsumptionInSeals;
        this.turbineCylinder = turbineCylinder;
    }

    public HashMap<Element, Double> getElementContributionToSteamConsumptionInSeals() {
        return elementContributionToSteamConsumptionInSeals;
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

        //--------------------------------Связи с элементами по линии греющего пара-------------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];
            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;

                if (element instanceof TurbineCylinder) {
                    TurbineCylinder turbineCylinder = (TurbineCylinder) element;
                    int materialBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(turbineCylinder.getMaterialBalanceEquation());
                    freeMemoryMatrix[materialBalanceEquation] += relations * elementContributionToSteamConsumptionInSeals.get(turbineCylinder);
                }

                if (element instanceof Separator) {
                    Separator separator = (Separator) element;
                    int materialBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(separator.getMaterialBalanceEquation());
                    int heatBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(separator.getHeatBalanceEquation());
                    freeMemoryMatrix[materialBalanceEquation] += relations * elementContributionToSteamConsumptionInSeals.get(separator);
                    freeMemoryMatrix[heatBalanceEquation] +=
                            relations * elementContributionToSteamConsumptionInSeals.get(separator) * turbineCylinder.parametersInSelection(0).getEnthalpy();

                }

                if (element instanceof Heater) {
                    Heater heater = (Heater) element;
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
        for (Map.Entry<Element, Double> elementsDoubleEntry : elementContributionToSteamConsumptionInSeals.entrySet()) {
            System.out.println("Элемент схемы: " + elementsDoubleEntry.getKey().NAME +
                    " Расход пара из (в) уплотнения: " + elementContributionToSteamConsumptionInSeals.get(elementsDoubleEntry.getKey()));
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }

}

package com.example.thermal_circuit_simulation.Elements.Seals;

import com.example.thermal_circuit_simulation.Elements.*;
import com.example.thermal_circuit_simulation.Graph.*;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.*;
import com.hummeling.if97.IF97;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.thermal_circuit_simulation.Graph.Graph.HEATING_STEAM;

/**
 * Уплотнения вала турбины
 */

public class TurbineShaftSeals extends Element implements MatrixCompilation {
    // TODO: 26.08.2019 Костыль, можно сделать лучше...
    /**
     * Вклад элемента в расход пара в уплотнениях
     */
    private HashMap<Element, Double> elementContributionToSteamConsumptionInSeals;
    private TurbineCylinder turbineCylinder;

    public TurbineShaftSeals(String name, HashMap<Element, Double> elementContributionToSteamConsumptionInSeals, TurbineCylinder turbineCylinder) {
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
        List<Vertex> vertexList = theGraph.getVertexList();
        double[][] coefficientMatrix = matrices.coefficientMatrix;
        double[] freeMemoryMatrix = matrices.freeMemoryMatrix;
        List<Consumptions> listOfConsumptions = matrices.getListOfColumnsOfConsumptions();

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


                if (element instanceof Heater) {
                    Heater heater = (Heater) element;
                    if (heater.isSurfaceHeater()) {
                        int materialBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(heater.getMaterialBalanceEquationOnSteamDrainLine());
                        int heatBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(heater.getHeatBalanceEquation());
                        freeMemoryMatrix[materialBalanceEquation] += relations * elementContributionToSteamConsumptionInSeals.get(heater);
                        freeMemoryMatrix[heatBalanceEquation] +=
                                relations * elementContributionToSteamConsumptionInSeals.get(heater) *
                                        turbineCylinder.parametersInSelection(turbineCylinder.NUMBER_OF_SELECTIONS + 1).getEnthalpy();
                    } else {
                        int materialBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(heater.getMaterialBalanceEquationOnHeatedMediumLine());
                        int heatBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(heater.getHeatBalanceEquation());
                        freeMemoryMatrix[materialBalanceEquation] += relations * elementContributionToSteamConsumptionInSeals.get(heater);
                        freeMemoryMatrix[heatBalanceEquation] +=
                                relations * elementContributionToSteamConsumptionInSeals.get(heater) *
                                        turbineCylinder.parametersInSelection(turbineCylinder.NUMBER_OF_SELECTIONS + 1).getEnthalpy();
                    }
                }

                if (element instanceof Deaerator) {
                    Deaerator deaerator = (Deaerator) element;
                    if (relations == 1) {
                        int materialBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(deaerator.getMaterialBalanceEquationOnHeatedMediumLine());
                        int heatBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(deaerator.getHeatBalanceEquation());
                        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
                        freeMemoryMatrix[materialBalanceEquation] += relations * elementContributionToSteamConsumptionInSeals.get(deaerator);
                        freeMemoryMatrix[heatBalanceEquation] +=
                                relations * elementContributionToSteamConsumptionInSeals.get(deaerator) *
                                        waterSteam.specificEnthalpySaturatedVapourP(deaerator.getPressureOfHeatedMedium());
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

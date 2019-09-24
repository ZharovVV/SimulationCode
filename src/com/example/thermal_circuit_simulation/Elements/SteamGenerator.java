package com.example.thermal_circuit_simulation.Elements;

import com.example.thermal_circuit_simulation.Graph.*;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Matrices;
import com.example.thermal_circuit_simulation.ThermalEfficiencyIndicators.ThermalEfficiencyIndicators;
import java.util.List;
import java.util.Map;
import static com.example.thermal_circuit_simulation.Graph.Graph.*;

public class SteamGenerator extends Element {
    private double steamConsumption;        // Расход пара через ПГ
    private double inletEnthalpy;           // Энтальпия на входе в ПГ (на выходе из последнего подогревателя)
    private double outletEnthalpy;          // Энтальпия на выходе из ПГ (на входе в цилиндр)

    public SteamGenerator(String name, double steamConsumption) {
        super(name);
        this.steamConsumption = steamConsumption;
    }

    public double getSteamConsumption() {
        return steamConsumption;
    }

    @Override
    public void describe() {
        super.describe();
        System.out.println("Расход пара (воды) через ПГ: " + steamConsumption + " ,кг/с");
        System.out.println("Энтальпия на входе в ПГ: " + inletEnthalpy + " ,кДж/кг");
        System.out.println("Энтальпия на выходе из ПГ: " + outletEnthalpy + " ,кДж/кг");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    @Override
    public void matrixCompilation(int v, Matrices matrices, Graph theGraph) {
        //--------------------------Инициализация-----------------------------------------------------------------------
        int nVerts = theGraph.getnVerts();
        Map<Integer, int[][]> adjMat = theGraph.getAdjMat();
        List<Vertex> vertexList = theGraph.getVertexList();
        //--------------------------------Связи с элементами по линии греющего пара-------------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];
            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;

                if (element instanceof TurbineCylinder) {
                    TurbineCylinder turbineCylinder = (TurbineCylinder) element;
                    outletEnthalpy = turbineCylinder.parametersInSelection(0).getEnthalpy();
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии питательной воды----------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(FEED_WATER)[v][j];
            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;

                if (element instanceof Heater) {
                    Heater heater = (Heater) element;
                    inletEnthalpy = heater.getEnthalpyOfHeatedMedium();
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
    }

    @Override
    public void calculationOfThermalEfficiencyIndicators(int v, ThermalEfficiencyIndicators thermalEfficiencyIndicators, Graph theGraph) {
        thermalEfficiencyIndicators.setSteamConsumptionToTheTurbine(steamConsumption);
        thermalEfficiencyIndicators.setInletEnthalpy(inletEnthalpy);
        thermalEfficiencyIndicators.setOutletEnthalpy(outletEnthalpy);
    }
}

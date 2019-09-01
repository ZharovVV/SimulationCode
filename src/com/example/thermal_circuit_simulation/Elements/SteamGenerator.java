package com.example.thermal_circuit_simulation.Elements;

import com.example.thermal_circuit_simulation.Graph.Graph;
import com.example.thermal_circuit_simulation.Graph.Vertex;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.CalculationOfThermalEfficiencyIndicators;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Matrices;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.MatrixCompilation;
import com.example.thermal_circuit_simulation.ThermalEfficiencyIndicators.ThermalEfficiencyIndicators;

import java.util.ArrayList;
import java.util.Map;

import static com.example.thermal_circuit_simulation.Graph.Graph.*;

public class SteamGenerator extends Elements implements MatrixCompilation, CalculationOfThermalEfficiencyIndicators {
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
        ArrayList<Vertex> vertexList = theGraph.getVertexList();
        //--------------------------------Связи с элементами по линии греющего пара-------------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];
            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;

                if (element.getClass() == TurbineCylinders.class) {
                    TurbineCylinders turbineCylinder = (TurbineCylinders) element;
                    outletEnthalpy = turbineCylinder.parametersInSelection(0).getEnthalpy();
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии питательной воды----------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(FEED_WATER)[v][j];
            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;

                if (element.getClass() == Heaters.class) {
                    Heaters heater = (Heaters) element;
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

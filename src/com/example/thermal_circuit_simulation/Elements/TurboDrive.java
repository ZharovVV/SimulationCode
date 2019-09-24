package com.example.thermal_circuit_simulation.Elements;

import com.example.thermal_circuit_simulation.Graph.*;
import com.example.thermal_circuit_simulation.ThermalEfficiencyIndicators.ThermalEfficiencyIndicators;
import com.hummeling.if97.IF97;
import java.util.List;
import java.util.Map;
import static com.example.thermal_circuit_simulation.Graph.Graph.*;

public class TurboDrive extends Element {
    private double mechanicalEfficiency;        //Механический КПД Турбопривода
    private double relativeInternalEfficiency;  //Относительный внутренний КПД ТП
    private double condenserPressure;           // Давление в конденсаторе ТП
    private double feedwaterFlow;               // Расход питательной воды через ПН
    private double turboPower;                // Мощность ТП
    private double steamConsumption;            // Расход пара через ТП
    private double inletEnthalpy;
    private double outletEnthalpy;
    private int selectionNumber;


    public TurboDrive(String name,
                      double mechanicalEfficiency,
                      double relativeInternalEfficiency,
                      double condenserPressure,
                      double feedwaterFlow,
                      Pump feedPump,
                      Superheater superheater) {
        // TODO: 01.09.2019 Нужен ли вообще этот конструктор?
        super(name);
        this.mechanicalEfficiency = mechanicalEfficiency;
        this.relativeInternalEfficiency = relativeInternalEfficiency;
        this.condenserPressure = condenserPressure;
        this.inletEnthalpy = superheater.getEnthalpyOfHeatedMedium();
        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
        this.outletEnthalpy =
                inletEnthalpy - relativeInternalEfficiency * (inletEnthalpy - waterSteam.specificEnthalpyPS(
                        condenserPressure,
                        waterSteam.specificEntropyPH(superheater.getPressureOfHeatedMedium(), superheater.getEnthalpyOfHeatedMedium())));
        this.turboPower = feedPump.getEnthalpyIncrease() * feedwaterFlow / 1000 / mechanicalEfficiency;
        this.steamConsumption = turboPower * 1000 / (inletEnthalpy - outletEnthalpy);
    }

    public TurboDrive(String name,
                      double relativeInternalEfficiency,
                      double condenserPressure,
                      double feedwaterFlow) {
        super(name);
        this.relativeInternalEfficiency = relativeInternalEfficiency;
        this.condenserPressure = condenserPressure;
        this.feedwaterFlow = feedwaterFlow;
    }

    @Override
    public void calculationOfInitialParameters(int v, Graph theGraph) {
        //--------------------------Инициализация-----------------------------------------------------------------------
        int nVerts = theGraph.getnVerts();
        Map<Integer, int[][]> adjMat = theGraph.getAdjMat();
        List<Vertex> vertexList = theGraph.getVertexList();
        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии греющего пара-------------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];
            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;

                if (element instanceof TurbineCylinder) {
                    TurbineCylinder turbineCylinder = (TurbineCylinder) element;
                    TurbineCylinder.Parameters parameters = turbineCylinder.parametersInSelection(selectionNumber);
                    this.inletEnthalpy = parameters.getEnthalpy();

                    this.outletEnthalpy =
                            inletEnthalpy - relativeInternalEfficiency * (inletEnthalpy - waterSteam.specificEnthalpyPS(
                                    condenserPressure,
                                    waterSteam.specificEntropyPH(parameters.getPressure(), parameters.getEnthalpy())));
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------Связи с элементами по линии греющего пара-------------------------------------
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(MECHANICAL_COMMUNICATION)[v][j];
            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;

                if (element instanceof Pump) {
                    Pump feedPump = (Pump) element;
                    this.mechanicalEfficiency = feedPump.getPumpDriveEfficiency();
                    this.turboPower = feedPump.getEnthalpyIncrease() * feedwaterFlow / 1000 / mechanicalEfficiency;
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        this.steamConsumption = turboPower * 1000 / (inletEnthalpy - outletEnthalpy);
    }

    @Override
    public void setSelectionNumber(int selectionNumber) {
        this.selectionNumber = selectionNumber;
    }

    public double getSteamConsumption() {
        return steamConsumption;
    }

    public int getSelectionNumber() {
        return selectionNumber;
    }

    @Override
    public void describe() {
        super.describe();
        System.out.println("Механический КПД Турбопривода: " + mechanicalEfficiency);
        System.out.println("Относительный внутренний КПД ТП: " + relativeInternalEfficiency);
        System.out.println("Давление в конденсаторе ТП: " + condenserPressure + " ,МПа");
        System.out.println("Мощность ТП: " + turboPower + " ,МВт");
        System.out.println("Энтальпия на входе: " + inletEnthalpy + " ,кДж/кг");
        System.out.println("Энтальпия на выходе: " + outletEnthalpy + " ,кДж/кг");
        System.out.println("Расход пара через ТП: " + steamConsumption + " ,кг/c");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    @Override
    public void calculationOfThermalEfficiencyIndicators(int v, ThermalEfficiencyIndicators thermalEfficiencyIndicators, Graph theGraph) {
        thermalEfficiencyIndicators.setTurboPower(turboPower);
    }
}



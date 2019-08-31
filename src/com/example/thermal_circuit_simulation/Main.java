package com.example.thermal_circuit_simulation;

import com.example.thermal_circuit_simulation.Elements.*;
import com.example.thermal_circuit_simulation.Elements.Ejectors.MainEjectorsWithCooler;
import com.example.thermal_circuit_simulation.Elements.Ejectors.SealEjectorsWithCooler;
import com.example.thermal_circuit_simulation.Elements.Seals.TurbineShaftSeals;
import com.example.thermal_circuit_simulation.Elements.Seals.ValveStemSeals;
import com.example.thermal_circuit_simulation.Graph.Graph;
import com.example.thermal_circuit_simulation.Graph.Vertex;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Matrices;
import com.example.thermal_circuit_simulation.ThermalEfficiencyIndicators.ThermalEfficiencyIndicators;

import java.util.*;

import static java.lang.Double.NaN;

public class Main {
    public Map<String, Elements> initializationOfElements() {
        Map<String, Elements> elementsMap = new HashMap<>();
        //--------------------------ПГ
        SteamGenerator pg = new SteamGenerator("ПГ", 1786.1);
        elementsMap.put(pg.NAME, pg);

        //--------------------------ЦСД
        TurbineCylinders csd = new TurbineCylinders("ЦСД", 3);
        csd.addSelection(0, 5.88, 0.995);
        csd.addSelection(1, 2.982, 0.929);
        csd.addSelection(2, 1.92, 0.902);
        csd.addSelection(3, 1.203, 0.881);
        csd.addSelection(4, 1.203, 0.881);
        elementsMap.put(csd.NAME, csd);
        //-----------------------Сепаратор
        Separator separator = new Separator("Сепаратор", 0.02, 0.999, csd);
        elementsMap.put(separator.NAME, separator);
        //-----------------------ПП1
        Superheaters pp1 = new Superheaters("ПП1", 1, 0.12, 0.02,
                NaN, 20, 1, csd, separator);
        elementsMap.put(pp1.NAME, pp1);
        //-----------------------ПП2
        Superheaters pp2 = new Superheaters("ПП2", 2, 0.2, 0.02,
                NaN, 22.2, 0, csd, pp1);
        elementsMap.put(pp2.NAME, pp2);
        //--------------------------ЦНД
        TurbineCylinders cnd = new TurbineCylinders("ЦНД", 4);
        cnd.addSelection(0, 1.118, 250);
        cnd.addSelection(1, 0.638, 193);
        cnd.addSelection(2, 0.340, 139);
        cnd.addSelection(3, 0.092, 0.945);
        cnd.addSelection(4, 0.025, 0.902);
        cnd.addSelection(5, 0.0039, 0.8755);
        elementsMap.put(cnd.NAME, cnd);
        //-------------------------Конденсатор
        Condenser condenser = new Condenser("Конденсатор", cnd);
        elementsMap.put(condenser.NAME, condenser);
        //-----------------------Деаэратор
        Deaerator d = new Deaerator("Деаэратор", 0.69, 3, csd);
        elementsMap.put(d.NAME, d);
        //-------------------------Конденсатный насос 1
        Pumps kn1 = new Pumps("КНI", 0.78, 0.9, condenser, true);
        elementsMap.put(kn1.NAME, kn1);
        //-------------------------Основной эжектор
        MainEjectorsWithCooler mainEjector = new MainEjectorsWithCooler("Основной Эжектор", 0.15, 2, 1.22, kn1);
        elementsMap.put(mainEjector.NAME, mainEjector);
        //-------------------------Эжектор уплотнений
        SealEjectorsWithCooler sealEjector = new SealEjectorsWithCooler("Эжектор Уплотнений", 0.15, 6, 1.06, mainEjector);
        elementsMap.put(sealEjector.NAME, sealEjector);
        //-------------------------Конденсатный насос 2
        Pumps kn2 = new Pumps("КНII", 0.78, 1.0, sealEjector, true);
        elementsMap.put(kn2.NAME, kn2);
        //-------------------------ПНД1
        Heaters pnd1 = new Heaters("ПНД1", 1, 0.15, NaN, 2.5, 4,
                cnd, kn2);
        elementsMap.put(pnd1.NAME, pnd1);
        //-------------------------ДН1
        Pumps dn1 = new Pumps("ДН1", true, 0.76, 2, pnd1);
        elementsMap.put(dn1.NAME, dn1);
        //-------------------------См1
        MixingPoints sm1 = new MixingPoints("См1", pnd1);
        elementsMap.put(sm1.NAME, sm1);
        //-------------------------ПНД2
        Heaters pnd2 = new Heaters("ПНД2", 2, 0.15, 5, 3, 3,
                cnd, pnd1);
        elementsMap.put(pnd2.NAME, pnd2);
        //------------------------ПНД3
        Heaters pnd3 = new Heaters("ПНД3", 3, 0.15, NaN, 4, 2,
                cnd, pnd2);
        elementsMap.put(pnd3.NAME, pnd3);
        //------------------------ДН2
        Pumps dn2 = new Pumps("ДН2", true, 0.76, 1.5, pnd3);
        elementsMap.put(dn2.NAME, dn2);
        //------------------------См2
        MixingPoints sm2 = new MixingPoints("См2", pnd3);
        elementsMap.put(sm2.NAME, sm2);
        //------------------------ПНД4
        Heaters pnd4 = new Heaters("ПНД4", 4, 0.15, 4.5, 4, 1,
                cnd, pnd3);
        elementsMap.put(pnd4.NAME, pnd4);
        //-----------------------ПН
        Pumps pn = new Pumps("ПН", 0.89, 8.9, d, false);
        elementsMap.put(pn.NAME, pn);
        //-----------------------ПВД5
        Heaters pvd5 = new Heaters("ПВД5", 5, 0.4, 5, 5, 3,
                csd, pn);
        elementsMap.put(pvd5.NAME, pvd5);
        //-----------------------ПВД6
        Heaters pvd6 = new Heaters("ПВД6", 6, 0.4, 5, 5.5, 2,
                csd, pvd5);
        elementsMap.put(pvd6.NAME, pvd6);
        //-----------------------ПВД7
        Heaters pvd7 = new Heaters("ПВД7", 7, 0.4, 5, 6.2, 1,
                csd, pvd6);
        elementsMap.put(pvd7.NAME, pvd7);
        //----------------------ТС
        HeatNetwork ts = new HeatNetwork("Теплосеть", 1, 150, 1.6, 60, 120);
        elementsMap.put(ts.NAME, ts);
        //----------------------Т1
        Heaters t1 = new Heaters("Т1", 1, 0.2, NaN, 4, 3,
                cnd, ts);
        elementsMap.put(t1.NAME, t1);
        //----------------------Т2
        Heaters t2 = new Heaters("Т2", 2, 0.2, NaN, 4.6, 2,
                cnd, t1);
        //t2.describeHeater();

        elementsMap.put(t2.NAME, t2);
        //----------------------Т3
        Heaters t3 = new Heaters("Т3", 3, 0.2, NaN, 8.8, 1,
                cnd, t2);
        elementsMap.put(t3.NAME, t3);
        //---------------------ТП
        TurboDrive turboDrive = new TurboDrive("ТП", 0.96, 0.73, 0.004, 1786.1,
                pn, 0, cnd
        );
        elementsMap.put(turboDrive.NAME, turboDrive);
        //-------------------------------------------

        HashMap<Elements, Double> mapForValveStemSeal = new HashMap<>();
        mapForValveStemSeal.put(csd, 1.8);
        mapForValveStemSeal.put(separator, 1.33);
        mapForValveStemSeal.put(pnd3, 0.37);
        mapForValveStemSeal.put(sealEjector, 0.1);
        ValveStemSeals valveStemSeal = new ValveStemSeals("Уплотнение штоков клапанов ЦСД", mapForValveStemSeal, csd);
        elementsMap.put(valveStemSeal.NAME, valveStemSeal);

        HashMap<Elements, Double> mapForTurbineShaftSealsForCSD = new HashMap<>();
        mapForTurbineShaftSealsForCSD.put(csd, 2.4);
        mapForTurbineShaftSealsForCSD.put(pnd4, 1.38);
        mapForTurbineShaftSealsForCSD.put(pnd1, 0.9);
        mapForTurbineShaftSealsForCSD.put(sealEjector, 0.15);
        TurbineShaftSeals turbineShaftSealForCSD = new TurbineShaftSeals("Уплотнение вала ЦСД", mapForTurbineShaftSealsForCSD, csd);
        elementsMap.put(turbineShaftSealForCSD.NAME, turbineShaftSealForCSD);

        HashMap<Elements, Double> mapForTurbineShaftSealsForCND = new HashMap<>();
        mapForTurbineShaftSealsForCND.put(cnd, 1.48);
        mapForTurbineShaftSealsForCND.put(d, 2.56);
        mapForTurbineShaftSealsForCND.put(sealEjector, 1.08);
        TurbineShaftSeals turbineShaftSealForCND = new TurbineShaftSeals("Уплотнение вала ЦНД", mapForTurbineShaftSealsForCND, cnd);
        elementsMap.put(turbineShaftSealForCND.NAME, turbineShaftSealForCND);

        System.out.println("Количество элементов " + elementsMap.size());
        return elementsMap;

    }

    public void runGraph(Map<String, Elements> elementsMap) {
        Graph theGraph = new Graph();
        Vertex pg = new Vertex(elementsMap.get("ПГ"));
        Vertex csd = new Vertex(elementsMap.get("ЦСД"));
        Vertex valveStemSeal = new Vertex(elementsMap.get("Уплотнение штоков клапанов ЦСД"));
        Vertex turbineShaftSealForCSD = new Vertex(elementsMap.get("Уплотнение вала ЦСД"));
        Vertex separator = new Vertex(elementsMap.get("Сепаратор"));
        Vertex pp1 = new Vertex(elementsMap.get("ПП1"));
        Vertex pp2 = new Vertex(elementsMap.get("ПП2"));
        Vertex cnd = new Vertex(elementsMap.get("ЦНД"));
        Vertex turbineShaftSealForCND = new Vertex(elementsMap.get("Уплотнение вала ЦНД"));
        Vertex condenser = new Vertex(elementsMap.get("Конденсатор"));
        Vertex kn1 = new Vertex(elementsMap.get("КНI"));
        Vertex mainEjector = new Vertex(elementsMap.get("Основной Эжектор"));
        Vertex sealEjector = new Vertex(elementsMap.get("Эжектор Уплотнений"));
        Vertex kn2 = new Vertex(elementsMap.get("КНII"));
        Vertex pnd1 = new Vertex(elementsMap.get("ПНД1"));
        Vertex dn1 = new Vertex(elementsMap.get("ДН1"));
        Vertex sm1 = new Vertex(elementsMap.get("См1"));
        Vertex pnd2 = new Vertex(elementsMap.get("ПНД2"));
        Vertex pnd3 = new Vertex(elementsMap.get("ПНД3"));
        Vertex dn2 = new Vertex(elementsMap.get("ДН2"));
        Vertex sm2 = new Vertex(elementsMap.get("См2"));
        Vertex pnd4 = new Vertex(elementsMap.get("ПНД4"));
        Vertex d = new Vertex(elementsMap.get("Деаэратор"));
        Vertex pn = new Vertex(elementsMap.get("ПН"));
        Vertex pvd5 = new Vertex(elementsMap.get("ПВД5"));
        Vertex pvd6 = new Vertex(elementsMap.get("ПВД6"));
        Vertex pvd7 = new Vertex(elementsMap.get("ПВД7"));
        Vertex ts = new Vertex(elementsMap.get("Теплосеть"));
        Vertex t1 = new Vertex(elementsMap.get("Т1"));
        Vertex t2 = new Vertex(elementsMap.get("Т2"));
        Vertex t3 = new Vertex(elementsMap.get("Т3"));
        Vertex turboDrive = new Vertex(elementsMap.get("ТП"));

        theGraph.addVertex(pg);
        theGraph.addVertex(csd);
        theGraph.addVertex(valveStemSeal);
        theGraph.addVertex(turbineShaftSealForCSD);
        theGraph.addVertex(separator);
        theGraph.addVertex(pp1);
        theGraph.addVertex(pp2);
        theGraph.addVertex(cnd);
        theGraph.addVertex(turbineShaftSealForCND);
        theGraph.addVertex(condenser);
        theGraph.addVertex(kn1);
        theGraph.addVertex(mainEjector);
        theGraph.addVertex(sealEjector);
        theGraph.addVertex(kn2);
        theGraph.addVertex(pnd1);
        theGraph.addVertex(dn1);
        theGraph.addVertex(sm1);
        theGraph.addVertex(pnd2);
        theGraph.addVertex(pnd3);
        theGraph.addVertex(dn2);
        theGraph.addVertex(sm2);
        theGraph.addVertex(pnd4);
        theGraph.addVertex(d);
        theGraph.addVertex(pn);
        theGraph.addVertex(pvd5);
        theGraph.addVertex(pvd6);
        theGraph.addVertex(pvd7);
        theGraph.addVertex(ts);
        theGraph.addVertex(t1);
        theGraph.addVertex(t2);
        theGraph.addVertex(t3);
        theGraph.addVertex(turboDrive);

        /*for (Elements element: elementsList) {
            theGraph.addVertex(new Vertex(element));
        }*/
        theGraph.addEdge(Graph.HEATING_STEAM, pg, csd);
        theGraph.addEdge(Graph.FEED_WATER, pvd7, pg);

        theGraph.addEdge(Graph.HEATING_STEAM, csd, separator);
        theGraph.addEdge(Graph.HEATING_STEAM, csd, pp2);
        theGraph.addEdge(Graph.HEATING_STEAM, csd, pp1);
        theGraph.addEdge(Graph.HEATING_STEAM, csd, pvd7);
        theGraph.addEdge(Graph.HEATING_STEAM, csd, pvd6);
        theGraph.addEdge(Graph.HEATING_STEAM, csd, pvd5);
        theGraph.addEdge(Graph.HEATING_STEAM, csd, d);
        theGraph.addEdge(Graph.HEATING_STEAM, csd, valveStemSeal);

        theGraph.addEdge(Graph.HEATING_STEAM, valveStemSeal, separator);
        theGraph.addEdge(Graph.HEATING_STEAM, valveStemSeal, pnd3);
        theGraph.addEdge(Graph.HEATING_STEAM, valveStemSeal, sealEjector);

        theGraph.addEdge(Graph.HEATING_STEAM, csd, turbineShaftSealForCSD);

        theGraph.addEdge(Graph.HEATING_STEAM, turbineShaftSealForCSD, pnd4);
        theGraph.addEdge(Graph.HEATING_STEAM, turbineShaftSealForCSD, pnd1);
        theGraph.addEdge(Graph.HEATING_STEAM, turbineShaftSealForCSD, sealEjector);

        theGraph.addEdge(Graph.SUPERHEATED_STEAM, separator, pp1);
        theGraph.addEdge(Graph.STEAM_DRAIN, separator, d);

        theGraph.addEdge(Graph.SUPERHEATED_STEAM, pp1, pp2);
        theGraph.addEdge(Graph.STEAM_DRAIN, pp1, pvd6);


        theGraph.addEdge(Graph.SUPERHEATED_STEAM, pp2, cnd);
        theGraph.addEdge(Graph.STEAM_DRAIN, pp2, pvd7);

        theGraph.addEdge(Graph.HEATING_STEAM, cnd, turboDrive);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, condenser);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, pnd4);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, pnd3);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, pnd2);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, pnd1);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, t3);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, t2);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, t1);

        theGraph.addEdge(Graph.HEATING_STEAM, turbineShaftSealForCND, cnd);
        theGraph.addEdge(Graph.HEATING_STEAM, turbineShaftSealForCND, sealEjector);
        theGraph.addEdge(Graph.HEATING_STEAM, d, turbineShaftSealForCND);

        theGraph.addEdge(Graph.FEED_WATER, condenser, kn1);
        theGraph.addEdge(Graph.FEED_WATER, kn1, mainEjector);

        theGraph.addEdge(Graph.STEAM_DRAIN, mainEjector, condenser);
        theGraph.addEdge(Graph.HEATING_STEAM, d, mainEjector);
        theGraph.addEdge(Graph.FEED_WATER, mainEjector, sealEjector);

        theGraph.addEdge(Graph.STEAM_DRAIN, sealEjector, condenser);
        theGraph.addEdge(Graph.HEATING_STEAM, d, sealEjector);
        theGraph.addEdge(Graph.HEATING_STEAM, valveStemSeal, sealEjector);
        theGraph.addEdge(Graph.HEATING_STEAM, turbineShaftSealForCSD, sealEjector);
        theGraph.addEdge(Graph.HEATING_STEAM, turbineShaftSealForCND, sealEjector);
        theGraph.addEdge(Graph.FEED_WATER, sealEjector, kn2);

        theGraph.addEdge(Graph.FEED_WATER, kn2, pnd1);

        theGraph.addEdge(Graph.FEED_WATER, pnd1, sm1);
        theGraph.addEdge(Graph.FEED_WATER, sm1, pnd2);
        theGraph.addEdge(Graph.STEAM_DRAIN, pnd1, dn1);
        theGraph.addEdge(Graph.STEAM_DRAIN, dn1, sm1);

        theGraph.addEdge(Graph.FEED_WATER, pnd2, pnd3);
        theGraph.addEdge(Graph.STEAM_DRAIN, pnd2, pnd1);

        theGraph.addEdge(Graph.FEED_WATER, pnd3, sm2);
        theGraph.addEdge(Graph.FEED_WATER, sm2, pnd4);
        theGraph.addEdge(Graph.STEAM_DRAIN, pnd3, dn2);
        theGraph.addEdge(Graph.STEAM_DRAIN, dn2, sm2);

        theGraph.addEdge(Graph.FEED_WATER, pnd4, d);
        theGraph.addEdge(Graph.STEAM_DRAIN, pnd4, pnd3);

        theGraph.addEdge(Graph.FEED_WATER, d, pn);

        theGraph.addEdge(Graph.FEED_WATER, pn, pvd5);

        theGraph.addEdge(Graph.FEED_WATER, pvd5, pvd6);
        theGraph.addEdge(Graph.STEAM_DRAIN, pvd5, pnd4);

        theGraph.addEdge(Graph.FEED_WATER, pvd6, pvd7);
        /*theGraph.addEdge(Graph.STEAM_DRAIN, pvd6, pvd5);*/
        theGraph.addEdge(Graph.STEAM_DRAIN, pvd6, d);

        theGraph.addEdge(Graph.STEAM_DRAIN, pvd7, pvd6);

        theGraph.addEdge(Graph.NETWORK_WATER, t3, ts);
        theGraph.addEdge(Graph.STEAM_DRAIN, t3, t2);
        theGraph.addEdge(Graph.NETWORK_WATER, ts, t1);
        theGraph.addEdge(Graph.NETWORK_WATER, t1, t2);
        theGraph.addEdge(Graph.STEAM_DRAIN, t1, condenser);
        theGraph.addEdge(Graph.NETWORK_WATER, t2, t3);
        theGraph.addEdge(Graph.STEAM_DRAIN, t2, t1);

        theGraph.addEdge(Graph.STEAM_DRAIN, turboDrive, condenser);

        /*theGraph.dfs();*/
        Matrices matrices = theGraph.dfsAndMatrixCompilation();
        /*matrices.describeMatrices();*/
        ArrayList<Vertex> vertexArrayList = theGraph.getVertexList();


        matrices.solvingSystemAndSettingConsumption();


        matrices = theGraph.dfsAndMatrixCompilation();
        matrices.solvingSystemAndSettingConsumption();
        matrices = theGraph.dfsAndMatrixCompilation();
        matrices.solvingSystemAndSettingConsumption();
        matrices = theGraph.dfsAndMatrixCompilation();
        matrices.solvingSystemAndSettingConsumption();
        matrices = theGraph.dfsAndMatrixCompilation();
        matrices.solvingSystemAndSettingConsumption();

        for (Vertex vertex : vertexArrayList) {
            vertex.element.describe();
        }

        ThermalEfficiencyIndicators thermalEfficiencyIndicators =
                theGraph.dfsAndCalculationOfThermalEfficiencyIndicators(0.988, 0.99);
        thermalEfficiencyIndicators.describe();
        /*Map<Integer,int[][]> map = theGraph.getAdjMat();
        for (Map.Entry<Integer, int[][]> entry : map.entrySet()) {
            System.out.println(entry.getKey());
            int[][] matrix = entry.getValue();
            for ( int i = 0; i<matrix.length;i++) {
                for (int j = 0; j<matrix[i].length;j++) {
                    System.out.print(matrix[i][j]);
                }
                System.out.println();
            }
        }*/

    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Map<String, Elements> elementsMap = new Main().initializationOfElements();
        new Main().runGraph(elementsMap);
        long finishTime = System.currentTimeMillis();
        System.out.println((finishTime - startTime) + " ms");
    }

}

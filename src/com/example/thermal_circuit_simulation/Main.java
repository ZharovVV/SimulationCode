package com.example.thermal_circuit_simulation;

import com.example.thermal_circuit_simulation.Elements.*;
import com.example.thermal_circuit_simulation.Elements.Ejectors.MainEjectorWithCooler;
import com.example.thermal_circuit_simulation.Elements.Ejectors.SealEjectorWithCooler;
import com.example.thermal_circuit_simulation.Elements.Seals.TurbineShaftSeals;
import com.example.thermal_circuit_simulation.Elements.Seals.ValveStemSeals;
import com.example.thermal_circuit_simulation.Graph.Graph;
import com.example.thermal_circuit_simulation.Graph.Vertex;
import com.example.thermal_circuit_simulation.ThermalEfficiencyIndicators.ThermalEfficiencyIndicators;

import java.util.*;

import static java.lang.Double.NaN;

public class Main {
    public Map<String, Element> initializationOfElements() {
        Map<String, Element> elementsMap = new HashMap<>();
        //--------------------------ПГ
        SteamGenerator pg = new SteamGenerator("ПГ", 1720);   //1786.1
        elementsMap.put(pg.NAME, pg);

        //--------------------------ЦСД
        TurbineCylinder csd = new TurbineCylinder("ЦСД", 3);
        csd.addSelection(0, 5.88, 0.995);
        csd.addSelection(1, 2.982, 0.929);
        csd.addSelection(2, 1.92, 0.902);
        csd.addSelection(3, 1.203, 0.881);
        csd.addSelection(4, 1.203, 0.881);
        elementsMap.put(csd.NAME, csd);
        //-----------------------Сепаратор
        Separator separator = new Separator("Сепаратор", 0.02, 0.999);
        elementsMap.put(separator.NAME, separator);
        //-----------------------ПП1
        Superheater pp1 = new Superheater("ПП1", 1, 0.12, 0.02, 20);
        elementsMap.put(pp1.NAME, pp1);
        //-----------------------ПП2
        Superheater pp2 = new Superheater("ПП2", 2, 0.2, 0.02, 22.2);
        elementsMap.put(pp2.NAME, pp2);
        //--------------------------ЦНД
        TurbineCylinder cnd = new TurbineCylinder("ЦНД", 4);
        cnd.addSelection(0, 1.118, 250);
        cnd.addSelection(1, 0.638, 193);
        cnd.addSelection(2, 0.340, 139);
        cnd.addSelection(3, 0.092, 0.945);
        cnd.addSelection(4, 0.025, 0.902);
        cnd.addSelection(5, 0.0039, 0.8755);
        elementsMap.put(cnd.NAME, cnd);
        //-------------------------Конденсатор
        Condenser condenser = new Condenser("Конденсатор");
        elementsMap.put(condenser.NAME, condenser);
        //-----------------------Деаэратор
        Deaerator d = new Deaerator("Деаэратор", 0.69);
        elementsMap.put(d.NAME, d);
        //-------------------------Конденсатный насос 1
        Pump kn1 = new Pump("КНI", 0.78, 0.9, true, 0.86);
        elementsMap.put(kn1.NAME, kn1);
        //-------------------------Основной эжектор
        MainEjectorWithCooler mainEjector = new MainEjectorWithCooler("Основной Эжектор", 0.15, 2, 1.22);
        elementsMap.put(mainEjector.NAME, mainEjector);
        //-------------------------Эжектор уплотнений
        SealEjectorWithCooler sealEjector = new SealEjectorWithCooler("Эжектор Уплотнений", 0.15, 6, 1.06);
        elementsMap.put(sealEjector.NAME, sealEjector);
        //-------------------------Конденсатный насос 2
        Pump kn2 = new Pump("КНII", 0.78, 1.0, true, 0.86);
        elementsMap.put(kn2.NAME, kn2);
        //-------------------------ПНД1
        Heater pnd1 = new Heater("ПНД1", 1, 0.15, 2.5);
        /*Heater pnd1 = new Heater("ПНД1", 1);//Смешивающий подогреватель*/
        elementsMap.put(pnd1.NAME, pnd1);
        /*//-------------------------Перекачивающий насос
        Pump n = new Pump("Насос", 0.76, 1.425, true, 0.86);
        elementsMap.put(n.NAME, n);*/
        //-------------------------ДН1
        Pump dn1 = new Pump("ДН1", 0.76, 2, true, 0.86);
        elementsMap.put(dn1.NAME, dn1);
        //-------------------------См1
        MixingPoint sm1 = new MixingPoint("См1");
        elementsMap.put(sm1.NAME, sm1);
        //-------------------------ПНД2
        Heater pnd2 = new Heater("ПНД2", 2, 0.15, 5, 3);
        elementsMap.put(pnd2.NAME, pnd2);
        //------------------------ПНД3
        Heater pnd3 = new Heater("ПНД3", 3, 0.15, 4);
        elementsMap.put(pnd3.NAME, pnd3);
        //------------------------ДН2
        Pump dn2 = new Pump("ДН2", 0.76, 1.5, true, 0.86);
        elementsMap.put(dn2.NAME, dn2);
        //------------------------См2
        MixingPoint sm2 = new MixingPoint("См2");
        elementsMap.put(sm2.NAME, sm2);
        //------------------------ПНД4
        Heater pnd4 = new Heater("ПНД4", 4, 0.15, 4.5, 4);
        elementsMap.put(pnd4.NAME, pnd4);
        //-----------------------ПН
        Pump pn = new Pump("ПН", 0.89, 8.9, false, 0.96);
        elementsMap.put(pn.NAME, pn);
        //-----------------------ПВД5
        Heater pvd5 = new Heater("ПВД5", 5, 0.4, 5, 5);
        elementsMap.put(pvd5.NAME, pvd5);
        //-----------------------ПВД6
        Heater pvd6 = new Heater("ПВД6", 6, 0.4, 5, 5.5);
        elementsMap.put(pvd6.NAME, pvd6);
        //-----------------------ПВД7
        Heater pvd7 = new Heater("ПВД7", 7, 0.4, 5, 6.2);
        elementsMap.put(pvd7.NAME, pvd7);
        //----------------------ТС
        HeatNetwork ts = new HeatNetwork("Теплосеть", 1, 150, 1.6, 60, 120);
        elementsMap.put(ts.NAME, ts);
        //----------------------Т1
        Heater t1 = new Heater("Т1", 1, 0.2, 4);
        elementsMap.put(t1.NAME, t1);
        //----------------------Т2
        Heater t2 = new Heater("Т2", 2, 0.2, 4.6);
        //t2.describeHeater();

        elementsMap.put(t2.NAME, t2);
        //----------------------Т3
        Heater t3 = new Heater("Т3", 3, 0.2, 8.8);
        elementsMap.put(t3.NAME, t3);
        //---------------------ТП
        TurboDrive turboDrive = new TurboDrive("ТП", 0.73, 0.004, 1720); //1786.1
        elementsMap.put(turboDrive.NAME, turboDrive);
        //-------------------------------------------

        HashMap<Element, Double> mapForValveStemSeal = new HashMap<>();
        mapForValveStemSeal.put(csd, 1.8);
        mapForValveStemSeal.put(separator, 1.33);
        mapForValveStemSeal.put(pnd3, 0.37);
        mapForValveStemSeal.put(sealEjector, 0.1);
        ValveStemSeals valveStemSeal = new ValveStemSeals("Уплотнение штоков клапанов ЦСД", mapForValveStemSeal, csd);
        elementsMap.put(valveStemSeal.NAME, valveStemSeal);

        HashMap<Element, Double> mapForTurbineShaftSealsForCSD = new HashMap<>();
        mapForTurbineShaftSealsForCSD.put(csd, 2.4);
        mapForTurbineShaftSealsForCSD.put(pnd4, 1.38);
        mapForTurbineShaftSealsForCSD.put(pnd1, 0.9);
        mapForTurbineShaftSealsForCSD.put(sealEjector, 0.15);
        TurbineShaftSeals turbineShaftSealForCSD = new TurbineShaftSeals("Уплотнение вала ЦСД", mapForTurbineShaftSealsForCSD, csd);
        elementsMap.put(turbineShaftSealForCSD.NAME, turbineShaftSealForCSD);

        HashMap<Element, Double> mapForTurbineShaftSealsForCND = new HashMap<>();
        mapForTurbineShaftSealsForCND.put(cnd, 1.48);
        mapForTurbineShaftSealsForCND.put(d, 2.56);
        mapForTurbineShaftSealsForCND.put(sealEjector, 1.08);
        TurbineShaftSeals turbineShaftSealForCND = new TurbineShaftSeals("Уплотнение вала ЦНД", mapForTurbineShaftSealsForCND, cnd);
        elementsMap.put(turbineShaftSealForCND.NAME, turbineShaftSealForCND);

        System.out.println("Количество элементов " + elementsMap.size());
        return elementsMap;

    }

    public void runGraph(Map<String, Element> elementsMap) {
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
        /*Vertex n = new Vertex(elementsMap.get("Насос"));*/
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
        /*theGraph.addVertex(n);*/
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

        /*for (Element element: elementsList) {
            theGraph.addVertex(new Vertex(element));
        }*/
        theGraph.addEdge(Graph.HEATING_STEAM, pg, csd);
        theGraph.addEdge(Graph.FEED_WATER, pvd7, pg);

        theGraph.addEdge(Graph.HEATING_STEAM, csd, separator);
        theGraph.addEdge(Graph.HEATING_STEAM, csd, 0, pp2);
        theGraph.addEdge(Graph.HEATING_STEAM, csd, 1, pp1);
        theGraph.addEdge(Graph.HEATING_STEAM, csd, 1, pvd7);
        theGraph.addEdge(Graph.HEATING_STEAM, csd, 2, pvd6);
        theGraph.addEdge(Graph.HEATING_STEAM, csd, 3, pvd5);
        theGraph.addEdge(Graph.HEATING_STEAM, csd, 3, d);
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

        theGraph.addEdge(Graph.HEATING_STEAM, cnd, 0, turboDrive);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, condenser);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, 1, pnd4);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, 2, pnd3);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, 3, pnd2);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, 4, pnd1);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, 1, t3);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, 2, t2);
        theGraph.addEdge(Graph.HEATING_STEAM, cnd, 3, t1);

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
        /*theGraph.addEdge(Graph.FEED_WATER, pnd1, n);
        theGraph.addEdge(Graph.FEED_WATER, n, pnd2);*/
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
        theGraph.addEdge(Graph.MECHANICAL_COMMUNICATION, turboDrive, pn);

        theGraph.startCalculation();
        theGraph.describe();


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
        Map<String, Element> elementsMap = new Main().initializationOfElements();
        new Main().runGraph(elementsMap);
        long finishTime = System.currentTimeMillis();
        System.out.println((finishTime - startTime) + " ms");
    }

}

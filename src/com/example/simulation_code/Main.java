package com.example.simulation_code;

import com.example.simulation_code.Elements.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public Map<String, Elements> initializationOfElements() throws FileNotFoundException {
        Map<String, Elements> elementsMap = new HashMap<>();
        Scanner scanner = new Scanner(new File("G:\\Vitaly\\Program Files\\IntelliJ IDEA\\IntelliJ IDEA Community Edition 2019.1.3\\IdeaProjects\\SimulationCode\\src\\com\\example\\simulation_code\\input.txt"));
        //--------------------------ПГ
        SteamGenerator pg = new SteamGenerator("ПГ", 1786.1);
        elementsMap.put(pg.NAME, pg);

        //--------------------------ЦСД
        TurbineCylinders csd = new TurbineCylinders(scanner.next(), Integer.parseInt(scanner.next()));
        csd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        csd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        csd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        csd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        csd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));

        elementsMap.put(csd.NAME, csd);
        //-----------------------Сепаратор
        Separator separator = new Separator(scanner.next(),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                csd);
        /*separator.describeSeparator();*/

        elementsMap.put(separator.NAME, separator);
        //-----------------------ПП1
        Superheaters pp1 = new Superheaters(
                scanner.next(),
                Integer.parseInt(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                csd,
                separator);
        /*pp1.describe();
*/
        elementsMap.put(pp1.NAME, pp1);
        //-----------------------ПП2
        Superheaters pp2 = new Superheaters(
                scanner.next(),
                Integer.parseInt(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                csd,
                pp1);
        /*pp2.describe();*/

        elementsMap.put(pp2.NAME, pp2);
        //--------------------------ЦНД
        TurbineCylinders cnd = new TurbineCylinders(scanner.next(), Integer.parseInt(scanner.next()));
        cnd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        cnd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        cnd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        cnd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        cnd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        cnd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));

        elementsMap.put(cnd.NAME, cnd);
        //-------------------------Конденсатор
        Condenser condenser = new Condenser(scanner.next(), cnd);
        //condenser.describeCondenser();

        elementsMap.put(condenser.NAME, condenser);
        //-------------------------Конденсатный насос
        Pumps kn = new Pumps(scanner.next(), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()), condenser);
        //kn.describePump();

        elementsMap.put(kn.NAME, kn);
        //-------------------------ПНД1
        Heaters pnd1 = new Heaters(
                scanner.next(),
                Integer.parseInt(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                cnd,
                kn
        );
        //pnd1.describeHeater();

        elementsMap.put(pnd1.NAME, pnd1);
        //-------------------------ПНД2
        Heaters pnd2 = new Heaters(
                scanner.next(),
                Integer.parseInt(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                cnd,
                pnd1
        );
        //pnd2.describeHeater();

        elementsMap.put(pnd2.NAME, pnd2);
        //------------------------ПНД3
        Heaters pnd3 = new Heaters(
                scanner.next(),
                Integer.parseInt(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                cnd,
                pnd2
        );
        //pnd3.describeHeater();

        elementsMap.put(pnd3.NAME, pnd3);
        //------------------------ПНД4
        Heaters pnd4 = new Heaters(
                scanner.next(),
                Integer.parseInt(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                cnd,
                pnd3
        );
        //pnd4.describeHeater();

        elementsMap.put(pnd4.NAME, pnd4);
        //-----------------------Деаэратор
        Heaters d = new Heaters(
                scanner.next(),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                csd,
                pnd4
        );
        //d.describeHeater();

        elementsMap.put(d.NAME, d);
        //-----------------------ПН
        Pumps pn = new Pumps(scanner.next(), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()), d);
        //pn.describePump();

        elementsMap.put(pn.NAME, pn);
        //-----------------------ПВД5
        Heaters pvd5 = new Heaters(
                scanner.next(),
                Integer.parseInt(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                csd,
                pn
        );
        //pvd5.describeHeater();

        elementsMap.put(pvd5.NAME, pvd5);
        //-----------------------ПВД6
        Heaters pvd6 = new Heaters(
                scanner.next(),
                Integer.parseInt(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                csd,
                pvd5
        );
        //pvd6.describeHeater();

        elementsMap.put(pvd6.NAME, pvd6);
        //-----------------------ПВД7
        Heaters pvd7 = new Heaters(
                scanner.next(),
                Integer.parseInt(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                csd,
                pvd6
        );
        //pvd7.describeHeater();

        elementsMap.put(pvd7.NAME, pvd7);
        //----------------------ТС
        HeatNetwork ts = new HeatNetwork(
                scanner.next(),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next())
        );
        //ts.describeHeatNetwork();

        elementsMap.put(ts.NAME, ts);
        //----------------------Т1
        Heaters t1 = new Heaters(
                scanner.next(),
                Integer.parseInt(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                cnd,
                ts
        );
        //t1.describeHeater();

        elementsMap.put(t1.NAME, t1);
        //----------------------Т2
        Heaters t2 = new Heaters(
                scanner.next(),
                Integer.parseInt(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                cnd,
                t1
        );
        //t2.describeHeater();

        elementsMap.put(t2.NAME, t2);
        //----------------------Т3
        Heaters t3 = new Heaters(
                scanner.next(),
                Integer.parseInt(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                cnd,
                t2
        );
        //t3.describeHeater();

        elementsMap.put(t3.NAME, t3);
        //---------------------ТП
        TurboDrive turboDrive = new TurboDrive(
                scanner.next(),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                pn,
                cnd,
                0
        );

        //turboDrive.describeTurboDrive();

        elementsMap.put(turboDrive.NAME, turboDrive);
        //-------------------------------------------
        System.out.println("Количество элементов " + elementsMap.size());
        return elementsMap;

    }

    public void runGraph(Map<String, Elements> elementsMap) {
        Graph theGraph = new Graph();
        Vertex pg = new Vertex(elementsMap.get("ПГ"));
        Vertex csd = new Vertex(elementsMap.get("ЦСД"));
        Vertex separator = new Vertex(elementsMap.get("Сепаратор"));
        Vertex pp1 = new Vertex(elementsMap.get("ПП1"));
        Vertex pp2 = new Vertex(elementsMap.get("ПП2"));
        Vertex cnd = new Vertex(elementsMap.get("ЦНД"));
        Vertex condenser = new Vertex(elementsMap.get("Конденсатор"));
        Vertex kn = new Vertex(elementsMap.get("КНI"));
        Vertex pnd1 = new Vertex(elementsMap.get("ПНД1"));
        Vertex pnd2 = new Vertex(elementsMap.get("ПНД2"));
        Vertex pnd3 = new Vertex(elementsMap.get("ПНД3"));
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
        theGraph.addVertex(separator);
        theGraph.addVertex(pp1);
        theGraph.addVertex(pp2);
        theGraph.addVertex(cnd);
        theGraph.addVertex(condenser);
        theGraph.addVertex(kn);
        theGraph.addVertex(pnd1);
        theGraph.addVertex(pnd2);
        theGraph.addVertex(pnd3);
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

        theGraph.addEdge(Graph.FEED_WATER, condenser, kn);

        theGraph.addEdge(Graph.FEED_WATER, kn, pnd1);

        theGraph.addEdge(Graph.FEED_WATER, pnd1, pnd2);
        theGraph.addEdge(Graph.STEAM_DRAIN, pnd1, condenser);

        theGraph.addEdge(Graph.FEED_WATER, pnd2, pnd3);
        theGraph.addEdge(Graph.STEAM_DRAIN, pnd2, pnd1);

        theGraph.addEdge(Graph.FEED_WATER, pnd3, pnd4);
        theGraph.addEdge(Graph.STEAM_DRAIN, pnd3, pnd2);

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

        theGraph.dfs();
        Matrices matrices = theGraph.dfsAndMatrixCompilation();
        matrices.describeMatrices();

        matrices.solvingSystemAndSettingConsumption();

        ArrayList<Vertex> vertexArrayList = theGraph.getVertexList();
        for (Vertex vertex : vertexArrayList) {
            vertex.element.describe();
        }
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

    public static void main(String[] args) throws FileNotFoundException {
        long startTime = System.currentTimeMillis();
        Map<String, Elements> elementsMap = new Main().initializationOfElements();
        new Main().runGraph(elementsMap);
        long finishTime = System.currentTimeMillis();
        System.out.println((finishTime - startTime) + " ms");
    }

}

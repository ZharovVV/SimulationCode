package com.example.simulation_code;

import org.apache.commons.math3.analysis.function.StepFunction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public List<Elements> run() throws FileNotFoundException {
        List<Elements> elementsList = new ArrayList<>();
        Scanner scanner = new Scanner(new File("G:\\Vitaly\\Program Files\\IntelliJ IDEA\\IntelliJ IDEA Community Edition 2019.1.3\\IdeaProjects\\SimulationCode\\src\\com\\example\\simulation_code\\input.txt"));
        //--------------------------ЦСД
        TurbineCylinders csd = new TurbineCylinders(scanner.next(), Integer.parseInt(scanner.next()));
        csd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        csd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        csd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        csd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        csd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));

        elementsList.add(csd);
        //-----------------------Сепаратор
        Separator separator = new Separator(scanner.next(),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                csd);
        //separator.describeSeparator();

        elementsList.add(separator);
        //-----------------------ПП1
        Superheaters pp1 = new Superheaters(
                scanner.next(),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                csd,
                separator);
        //pp1.describeSuperheater();

        elementsList.add(pp1);
        //-----------------------ПП2
        Superheaters pp2 = new Superheaters(
                scanner.next(),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                csd,
                pp1);
        //pp2.describeSuperheater();

        elementsList.add(pp2);
        //--------------------------ЦНД
        TurbineCylinders cnd = new TurbineCylinders(scanner.next(), Integer.parseInt(scanner.next()));
        cnd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        cnd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        cnd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        cnd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        cnd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
        cnd.addSelection(Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));

        elementsList.add(cnd);
        //-------------------------Конденсатор
        Condenser condenser = new Condenser(scanner.next(), cnd);
        //condenser.describeCondenser();

        elementsList.add(condenser);
        //-------------------------Конденсатный насос
        Pumps kn = new Pumps(scanner.next(), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()), condenser);
        //kn.describePump();

        elementsList.add(kn);
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

        elementsList.add(pnd1);
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

        elementsList.add(pnd2);
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

        elementsList.add(pnd3);
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

        elementsList.add(pnd4);
        //-----------------------Деаэратор
        Heaters d = new Heaters(
                scanner.next(),
                Double.parseDouble(scanner.next()),
                Integer.parseInt(scanner.next()),
                csd,
                pnd4
        );
        //d.describeHeater();

        elementsList.add(d);
        //-----------------------ПН
        Pumps pn = new Pumps(scanner.next(), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()), d);
        //pn.describePump();

        elementsList.add(pn);
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

        elementsList.add(pvd5);
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

        elementsList.add(pvd6);
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

        elementsList.add(pvd7);
        //----------------------ТС
        HeatNetwork ts = new HeatNetwork(
                scanner.next(),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next())
        );
        ts.describeHeatNetwork();

        elementsList.add(ts);
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
        t1.describeHeater();

        elementsList.add(t1);
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
        t2.describeHeater();

        elementsList.add(t2);
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
        t3.describeHeater();

        elementsList.add(t3);
        //---------------------ТП
        TurboDrive turboDrive = new TurboDrive(
                scanner.next(),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                Double.parseDouble(scanner.next()),
                pn,
                pp2
        );

        turboDrive.describeTurboDrive();

        elementsList.add(turboDrive);
        //-------------------------------------------
        System.out.println("Количество элементов " + elementsList.size());
        return elementsList;

    }

    public void runGraph(List<Elements> elementsList) {
        Graph theGraph = new Graph();
        Vertex csd = new Vertex(elementsList.get(0));
        Vertex separator = new Vertex(elementsList.get(1));
        Vertex pp1 = new Vertex(elementsList.get(2));
        Vertex pp2 = new Vertex(elementsList.get(3));
        Vertex cnd = new Vertex(elementsList.get(4));
        Vertex condenser = new Vertex(elementsList.get(5));
        Vertex kn = new Vertex(elementsList.get(6));
        Vertex pnd1 = new Vertex(elementsList.get(7));
        Vertex pnd2 = new Vertex(elementsList.get(8));
        Vertex pnd3 = new Vertex(elementsList.get(9));
        Vertex pnd4 = new Vertex(elementsList.get(10));
        Vertex d = new Vertex(elementsList.get(11));
        Vertex pn = new Vertex(elementsList.get(12));
        Vertex pvd5 = new Vertex(elementsList.get(13));
        Vertex pvd6 = new Vertex(elementsList.get(14));
        Vertex pvd7 = new Vertex(elementsList.get(15));
        Vertex ts = new Vertex(elementsList.get(16));
        Vertex t1 = new Vertex(elementsList.get(17));
        Vertex t2 = new Vertex(elementsList.get(18));
        Vertex t3 = new Vertex(elementsList.get(19));
        Vertex turboDrive = new Vertex(elementsList.get(20));

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

        theGraph.addEdge(Graph.SUPERHEATED_STEAM, csd, separator);
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
        theGraph.addEdge(Graph.SUPERHEATED_STEAM, pp2, turboDrive);
        theGraph.addEdge(Graph.STEAM_DRAIN, pp2, pvd7);

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
        theGraph.addEdge(Graph.STEAM_DRAIN, pnd3, condenser);

        theGraph.addEdge(Graph.FEED_WATER, pnd4, d);
        theGraph.addEdge(Graph.STEAM_DRAIN, pnd4, pnd3);

        theGraph.addEdge(Graph.FEED_WATER, d, pn);

        theGraph.addEdge(Graph.FEED_WATER, pn, pvd5);

        theGraph.addEdge(Graph.FEED_WATER, pvd5, pvd6);
        theGraph.addEdge(Graph.STEAM_DRAIN, pvd5, pnd4);

        theGraph.addEdge(Graph.FEED_WATER, pvd6, pvd7);
        theGraph.addEdge(Graph.STEAM_DRAIN, pvd6, pvd5);
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
        List<Elements> elementsArrayList = new Main().run();
        new Main().runGraph(elementsArrayList);
        long finishTime = System.currentTimeMillis();
        System.out.println((finishTime - startTime) + " ms");
    }

}

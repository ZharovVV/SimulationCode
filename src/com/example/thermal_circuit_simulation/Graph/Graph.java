package com.example.thermal_circuit_simulation.Graph;

import com.example.thermal_circuit_simulation.Elements.*;
import com.example.thermal_circuit_simulation.Elements.Ejectors.MainEjectorsWithCooler;
import com.example.thermal_circuit_simulation.Elements.Ejectors.SealEjectorsWithCooler;
import com.example.thermal_circuit_simulation.Elements.Seals.TurbineShaftSeals;
import com.example.thermal_circuit_simulation.Elements.Seals.ValveStemSeals;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Matrices;
import com.example.thermal_circuit_simulation.ThermalEfficiencyIndicators.ThermalEfficiencyIndicators;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph {
    private final int MAX_VERTS = 100;
    private ArrayList<Vertex> vertexList;
    public static final int FEED_WATER = 1;
    public static final int NETWORK_WATER = 2;
    public static final int HEATING_STEAM = 3;
    public static final int STEAM_DRAIN = 4;
    public static final int SUPERHEATED_STEAM = 5;
    private Map<Integer, int[][]> adjMat;        // Матрица смежности для каждой среды
    private int nVerts; //Текущее количество вершин
    private ArrayDeque<Integer> stack;

    public Graph() {
        nVerts = 0;
        adjMat = new HashMap<>();
        adjMat.put(FEED_WATER, new int[MAX_VERTS][MAX_VERTS]);
        adjMat.put(NETWORK_WATER, new int[MAX_VERTS][MAX_VERTS]);
        adjMat.put(HEATING_STEAM, new int[MAX_VERTS][MAX_VERTS]);
        adjMat.put(STEAM_DRAIN, new int[MAX_VERTS][MAX_VERTS]);
        adjMat.put(SUPERHEATED_STEAM, new int[MAX_VERTS][MAX_VERTS]);
        vertexList = new ArrayList<>();
        stack = new ArrayDeque<>();
    }

    public void addVertex(Vertex vertex) {
        nVerts++;
        vertexList.add(vertex);
    }

    public void addEdge(int mediumType, Vertex initialVertex, Vertex finalVertex) {
        int[][] matrix = adjMat.get(mediumType);
        int start = vertexList.indexOf(initialVertex);
        int end = vertexList.indexOf(finalVertex);
        matrix[start][end] = -1;
        matrix[end][start] = 1;
    }

    public void displayVertex(int v) {
        System.out.println(vertexList.get(v).element.NAME);
    }

    public void dfs() {
        vertexList.get(0).wasVisited = true;
        displayVertex(0);
        stack.add(0);

        while (!stack.isEmpty()) {
            int v = getAdjUnvisitedVertex(stack.peekLast());
            if (v == -3) {          // Если такой вершины нет,
                stack.pollLast();     // элемент извлекается из стека
            } else {                // Если вершина найдена
                vertexList.get(v).wasVisited = true;    // Пометка
                displayVertex(v);                   // Вывод
                stack.addLast(v);                   // Занесение в стек
            }
        }

        for (int i = 0; i < nVerts; i++) {
            vertexList.get(i).wasVisited = false;
        }
    }

    public Matrices dfsAndMatrixCompilation() {
        Matrices matrices = new Matrices(vertexList);
        vertexList.get(0).wasVisited = true;
        stack.add(0);

        while (!stack.isEmpty()) {
            int v = getAdjUnvisitedVertex(stack.peekLast());
            if (v == -3) {          // Если такой вершины нет,
                stack.pollLast();     // элемент извлекается из стека
            } else {                // Если вершина найдена
                vertexList.get(v).wasVisited = true;    // Пометка
                matrixCompilation(v, matrices);                   // Составление матриц
                stack.addLast(v);                   // Занесение в стек
            }
        }

        for (int i = 0; i < nVerts; i++) {
            vertexList.get(i).wasVisited = false;
        }

        return matrices;
    }

    public ThermalEfficiencyIndicators dfsAndCalculationOfThermalEfficiencyIndicators(double generatorEfficiency, double mechanicalEfficiencyOfTurbogenerator) {
        ThermalEfficiencyIndicators thermalEfficiencyIndicators = new ThermalEfficiencyIndicators(generatorEfficiency, mechanicalEfficiencyOfTurbogenerator);
        vertexList.get(0).wasVisited = true;
        stack.add(0);

        while (!stack.isEmpty()) {
            int v = getAdjUnvisitedVertex(stack.peekLast());
            if (v == -3) {          // Если такой вершины нет,
                stack.pollLast();     // элемент извлекается из стека
            } else {                // Если вершина найдена
                vertexList.get(v).wasVisited = true;    // Пометка
                calculationOfThermalEfficiencyIndicators(v, thermalEfficiencyIndicators);     // Расчет показателей тепловой экономичности, связанных с элементом v.
                stack.addLast(v);                   // Занесение в стек
            }
        }

        for (int i = 0; i < nVerts; i++) {
            vertexList.get(i).wasVisited = false;
        }

        thermalEfficiencyIndicators.calculationOfInternalCompartmentPower();
        thermalEfficiencyIndicators.calculationOfGuaranteedElectricPower();

        return thermalEfficiencyIndicators;
    }

    private void calculationOfThermalEfficiencyIndicators(int v, ThermalEfficiencyIndicators thermalEfficiencyIndicators) {
        Elements element = vertexList.get(v).element;
        if (element.getClass() == TurbineCylinders.class) {
            TurbineCylinders turbineCylinder = (TurbineCylinders) element;
            turbineCylinder.calculationOfThermalEfficiencyIndicators(v, thermalEfficiencyIndicators, this);
        }

        if (element.getClass() == Pumps.class) {
            Pumps pump = (Pumps) element;
            pump.calculationOfThermalEfficiencyIndicators(v, thermalEfficiencyIndicators, this);
        }

    }


    private int getAdjUnvisitedVertex(int v) {
        for (int j = 0; j < nVerts; j++) {
            if (adjMat.get(SUPERHEATED_STEAM)[v][j] == -1 && !vertexList.get(j).wasVisited) {
                return j;
            }
        }
        for (int j = 0; j < nVerts; j++) {
            if (adjMat.get(HEATING_STEAM)[v][j] == -1 && !vertexList.get(j).wasVisited) {
                return j;
            }
        }

        for (int j = 0; j < nVerts; j++) {
            if (adjMat.get(FEED_WATER)[v][j] == -1 && !vertexList.get(j).wasVisited) {
                return j;
            }
        }

        for (int j = 0; j < nVerts; j++) {
            if (adjMat.get(NETWORK_WATER)[v][j] == -1 && !vertexList.get(j).wasVisited) {
                return j;
            }
        }

        for (int j = 0; j < nVerts; j++) {
            if (adjMat.get(STEAM_DRAIN)[v][j] == -1 && !vertexList.get(j).wasVisited) {
                return j;
            }
        }

        return -3;
    }

    @SuppressWarnings("ConstantConditions")
    private void matrixCompilation(int v, Matrices matrices) {
        Elements element = vertexList.get(v).element;
        if (element.getClass() == TurbineCylinders.class) {
            TurbineCylinders turbineCylinder = (TurbineCylinders) element;
            turbineCylinder.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == Separator.class) {
            Separator separator = (Separator) element;
            separator.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == Superheaters.class) {
            Superheaters superheater = (Superheaters) element;
            superheater.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == Condenser.class) {
            Condenser condenser = (Condenser) element;
            condenser.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == Pumps.class) {
            Pumps pump = (Pumps) element;
            pump.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == Heaters.class) {
            Heaters heater = (Heaters) element;
            heater.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == Deaerator.class) {
            Deaerator deaerator = (Deaerator) element;
            deaerator.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == MixingPoints.class) {
            MixingPoints mixingPoint = (MixingPoints) element;
            mixingPoint.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == ValveStemSeals.class) {
            ValveStemSeals valveStemSeal = (ValveStemSeals) element;
            valveStemSeal.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == TurbineShaftSeals.class) {
            TurbineShaftSeals turbineShaftSeal = (TurbineShaftSeals) element;
            turbineShaftSeal.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == MainEjectorsWithCooler.class) {
            MainEjectorsWithCooler mainEjectorWithCooler = (MainEjectorsWithCooler) element;
            mainEjectorWithCooler.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == SealEjectorsWithCooler.class) {
            SealEjectorsWithCooler sealEjectorWithCooler = (SealEjectorsWithCooler) element;
            sealEjectorWithCooler.matrixCompilation(v, matrices, this);
        }
    }


    public Map<Integer, int[][]> getAdjMat() {
        return adjMat;
    }

    public ArrayList<Vertex> getVertexList() {
        return vertexList;
    }

    public int getnVerts() {
        return nVerts;
    }
}







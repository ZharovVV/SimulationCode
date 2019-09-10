package com.example.thermal_circuit_simulation.Graph;

import com.example.thermal_circuit_simulation.Elements.*;
import com.example.thermal_circuit_simulation.Elements.Ejectors.MainEjectorWithCooler;
import com.example.thermal_circuit_simulation.Elements.Ejectors.SealEjectorWithCooler;
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
    public static final int MECHANICAL_COMMUNICATION = 6;
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
        adjMat.put(MECHANICAL_COMMUNICATION, new int[MAX_VERTS][MAX_VERTS]);
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

    // Метод для связи между цилиндром и подогревателем
    public void addEdge(int mediumType, Vertex initialVertex, int selectionNumber, Vertex finalVertex) {
        int[][] matrix = adjMat.get(mediumType);
        int start = vertexList.indexOf(initialVertex);
        int end = vertexList.indexOf(finalVertex);
        matrix[start][end] = -1;
        matrix[end][start] = 1;
        finalVertex.element.setSelectionNumber(selectionNumber);
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

    public void dfsAndCalculationOfInitialParameters() {
        vertexList.get(0).wasVisited = true;
        calculationOfInitialParameters(0);
        stack.add(0);

        while (!stack.isEmpty()) {
            int v = getAdjUnvisitedVertex(stack.peekLast());
            if (v == -3) {          // Если такой вершины нет,
                stack.pollLast();     // элемент извлекается из стека
            } else {                // Если вершина найдена
                vertexList.get(v).wasVisited = true;    // Пометка
                calculationOfInitialParameters(v);      // Вычисление начальных параметров элемента
                stack.addLast(v);                   // Занесение в стек
            }
        }

        for (int i = 0; i < nVerts; i++) {
            vertexList.get(i).wasVisited = false;
        }
    }

    private void calculationOfInitialParameters(int v) {
        Element element = vertexList.get(v).element;
        element.calculationOfInitialParameters(v, this);
    }

    public Matrices dfsAndMatrixCompilation() {
        Matrices matrices = new Matrices(vertexList);
        vertexList.get(0).wasVisited = true;
        matrixCompilation(0, matrices);
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

    @SuppressWarnings("ConstantConditions")
    private void matrixCompilation(int v, Matrices matrices) {
        Element element = vertexList.get(v).element;
        // TODO: 01.09.2019 Переделать метод! Можно написать проще.
        if (element.getClass() == SteamGenerator.class) {
            SteamGenerator steamGenerator = (SteamGenerator) element;
            steamGenerator.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == TurbineCylinder.class) {
            TurbineCylinder turbineCylinder = (TurbineCylinder) element;
            turbineCylinder.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == Separator.class) {
            Separator separator = (Separator) element;
            separator.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == Superheater.class) {
            Superheater superheater = (Superheater) element;
            superheater.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == Condenser.class) {
            Condenser condenser = (Condenser) element;
            condenser.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == Pump.class) {
            Pump pump = (Pump) element;
            pump.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == Heater.class) {
            Heater heater = (Heater) element;
            heater.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == Deaerator.class) {
            Deaerator deaerator = (Deaerator) element;
            deaerator.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == MixingPoint.class) {
            MixingPoint mixingPoint = (MixingPoint) element;
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

        if (element.getClass() == MainEjectorWithCooler.class) {
            MainEjectorWithCooler mainEjectorWithCooler = (MainEjectorWithCooler) element;
            mainEjectorWithCooler.matrixCompilation(v, matrices, this);
        }

        if (element.getClass() == SealEjectorWithCooler.class) {
            SealEjectorWithCooler sealEjectorWithCooler = (SealEjectorWithCooler) element;
            sealEjectorWithCooler.matrixCompilation(v, matrices, this);
        }
    }


    public ThermalEfficiencyIndicators dfsAndCalculationOfThermalEfficiencyIndicators(double generatorEfficiency, double mechanicalEfficiencyOfTurbogenerator) {
        ThermalEfficiencyIndicators thermalEfficiencyIndicators = new ThermalEfficiencyIndicators(generatorEfficiency, mechanicalEfficiencyOfTurbogenerator);
        vertexList.get(0).wasVisited = true;
        calculationOfThermalEfficiencyIndicators(0, thermalEfficiencyIndicators);
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
        thermalEfficiencyIndicators.calculationOfElectricityConsumptionForOwnNeeds();
        thermalEfficiencyIndicators.calculationOfHeatConsumptionForATurbineForElectricityGeneration();
        thermalEfficiencyIndicators.calculationOfSpecificGrossHeatConsumptionForElectricityProduction();
        thermalEfficiencyIndicators.calculationOfElectricalEfficiency();

        return thermalEfficiencyIndicators;
    }

    @SuppressWarnings("ConstantConditions")
    private void calculationOfThermalEfficiencyIndicators(int v, ThermalEfficiencyIndicators thermalEfficiencyIndicators) {
        Element element = vertexList.get(v).element;
        // TODO: 01.09.2019 Переделать метод! Можно написать проще.
        if (element.getClass() == TurbineCylinder.class) {
            TurbineCylinder turbineCylinder = (TurbineCylinder) element;
            turbineCylinder.calculationOfThermalEfficiencyIndicators(v, thermalEfficiencyIndicators, this);
        }

        if (element.getClass() == Pump.class) {
            Pump pump = (Pump) element;
            pump.calculationOfThermalEfficiencyIndicators(v, thermalEfficiencyIndicators, this);
        }

        if (element.getClass() == SteamGenerator.class) {
            SteamGenerator steamGenerator = (SteamGenerator) element;
            steamGenerator.calculationOfThermalEfficiencyIndicators(v, thermalEfficiencyIndicators, this);
        }

        if (element.getClass() == HeatNetwork.class) {
            HeatNetwork heatNetwork = (HeatNetwork) element;
            heatNetwork.calculationOfThermalEfficiencyIndicators(v, thermalEfficiencyIndicators, this);
        }

        if (element.getClass() == TurboDrive.class) {
            TurboDrive turboDrive = (TurboDrive) element;
            turboDrive.calculationOfThermalEfficiencyIndicators(v, thermalEfficiencyIndicators, this);
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
            if (adjMat.get(STEAM_DRAIN)[v][j] == -1 && !vertexList.get(j).wasVisited) {
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



        return -3;
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








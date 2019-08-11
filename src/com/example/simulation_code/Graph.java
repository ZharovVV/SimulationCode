package com.example.simulation_code;

import com.example.simulation_code.Elements.*;
import com.example.simulation_code.HelperСlasses.Consumptions;

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
        int[][] matrix = new int[MAX_VERTS][MAX_VERTS];
        for (int i = 0; i < MAX_VERTS; i++) {
            for (int j = 0; j < MAX_VERTS; j++) {
                matrix[i][j] = 0;
            }
        }
        adjMat.put(FEED_WATER, matrix);
        adjMat.put(NETWORK_WATER, matrix);
        adjMat.put(HEATING_STEAM, matrix);
        adjMat.put(STEAM_DRAIN, matrix);
        adjMat.put(SUPERHEATED_STEAM, matrix);
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
        displayVertex(0);
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

        return -3;
    }

    @SuppressWarnings("ConstantConditions")
    private void matrixCompilation(int v, Matrices matrices) {
        Elements element = vertexList.get(v).element;
        if (element.getClass() == TurbineCylinders.class) {
            TurbineCylinders turbineCylinder = (TurbineCylinders) element;
            matrixCompilationForTurbineCylinders(v, matrices, turbineCylinder);
        }

        if (element.getClass() == Separator.class) {
            Separator separator = (Separator) element;
            matrixCompilationForSeparator(v, matrices, separator);
        }

        if (element.getClass() == Superheaters.class) {
            Superheaters superheater = (Superheaters) element;
            matrixCompilationForSuperheaters(v, matrices, superheater);
        }

        if (element.getClass() == Condenser.class) {
            Condenser condenser = (Condenser) element;
            matrixCompilationForCondenser(v,matrices,condenser);
        }


    }


    @SuppressWarnings("ConstantConditions")
    private void matrixCompilationForTurbineCylinders(int v, Matrices matrices, TurbineCylinders turbineCylinder) {
        double[][] coefficientMatrix = matrices.coefficientMatrix;
        double[] freeMemoryMatrix = matrices.freeMemoryMatrix;
        ArrayList<Consumptions> listOfConsumptions = matrices.getListOfConsumptions();
        int indexOfListOfEquation = matrices.getListOfEquation().indexOf(turbineCylinder.getMaterialBalanceEquation());
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(SUPERHEATED_STEAM)[v][j];

            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;
                if (element.getClass() == SteamGenerator.class) {
                    SteamGenerator steamGenerator = (SteamGenerator) element;
                    freeMemoryMatrix[indexOfListOfEquation] = (-1) * relations * steamGenerator.getSteamСonsumption();
                }

                if (element.getClass() == Superheaters.class) {
                    Superheaters superheater = (Superheaters) element;
                    int indexOfListConsumption = listOfConsumptions.indexOf(superheater.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == Heaters.class) {
                    Heaters heater = (Heaters) element;
                    int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == Separator.class) {
                    Separator separator = (Separator) element;
                    int indexOfListConsumption = listOfConsumptions.indexOf(separator.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == Condenser.class) {
                    Condenser condenser = (Condenser) element;
                    int indexOfListConsumption = listOfConsumptions.indexOf(condenser.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == TurboDrive.class) {
                    TurboDrive turboDrive = (TurboDrive) element;
                    freeMemoryMatrix[indexOfListOfEquation] = (-1) * relations * turboDrive.getSteamConsumption();
                }
            }
        }
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];

            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;
                if (element.getClass() == SteamGenerator.class) {
                    SteamGenerator steamGenerator = (SteamGenerator) element;
                    freeMemoryMatrix[indexOfListOfEquation] = (-1) * relations * steamGenerator.getSteamСonsumption();
                }

                if (element.getClass() == Superheaters.class) {
                    Superheaters superheater = (Superheaters) element;
                    int indexOfListConsumption = listOfConsumptions.indexOf(superheater.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == Heaters.class) {
                    Heaters heater = (Heaters) element;
                    int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == Separator.class) {
                    Separator separator = (Separator) element;
                    int indexOfListConsumption = listOfConsumptions.indexOf(separator.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == Condenser.class) {
                    Condenser condenser = (Condenser) element;
                    int indexOfListConsumption = listOfConsumptions.indexOf(condenser.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == TurboDrive.class) {
                    TurboDrive turboDrive = (TurboDrive) element;
                    freeMemoryMatrix[indexOfListOfEquation] = (-1) * relations * turboDrive.getSteamConsumption();
                }
            }
        }
    }

    private void matrixCompilationForSeparator(int v, Matrices matrices, Separator separator) {
        double[][] coefficientMatrix = matrices.coefficientMatrix;
        double[] freeMemoryMatrix = matrices.freeMemoryMatrix;
        ArrayList<Consumptions> listOfConsumptions = matrices.getListOfConsumptions();
        int materialBalanceEquation = matrices.getListOfEquation().indexOf(separator.getMaterialBalanceEquation());
        int heatBalanceEquation = matrices.getListOfEquation().indexOf(separator.getHeatBalanceEquation());
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(SUPERHEATED_STEAM)[v][j];
            int separatorIndexOfListConsumption = listOfConsumptions.indexOf(separator.getConsumptionOfHeatedMedium());
            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;

                if (element.getClass() == Superheaters.class) {
                    coefficientMatrix[materialBalanceEquation][separatorIndexOfListConsumption] = relations;
                    coefficientMatrix[heatBalanceEquation][separatorIndexOfListConsumption] = relations * separator.getEnthalpyOfHeatedMedium();
                }

                if (element.getClass() == TurbineCylinders.class) {
                    coefficientMatrix[materialBalanceEquation][separatorIndexOfListConsumption] = relations;
                    if (relations == 1) {
                        coefficientMatrix[heatBalanceEquation][separatorIndexOfListConsumption] = relations * separator.getEnthalpyOfHeatingSteam();
                    } else {
                        coefficientMatrix[heatBalanceEquation][separatorIndexOfListConsumption] = relations * separator.getEnthalpyOfHeatedMedium();
                    }
                }
            }
        }
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];
            int separatorIndexOfListConsumption = listOfConsumptions.indexOf(separator.getConsumptionOfHeatingSteam());
            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;
                if (element.getClass() == TurbineCylinders.class) {
                    coefficientMatrix[materialBalanceEquation][separatorIndexOfListConsumption] = relations;
                    if (relations == 1) {
                        coefficientMatrix[heatBalanceEquation][separatorIndexOfListConsumption] = relations * separator.getEnthalpyOfHeatingSteam();
                    } else {
                        coefficientMatrix[heatBalanceEquation][separatorIndexOfListConsumption] = relations * separator.getEnthalpyOfHeatedMedium();
                    }
                }
            }
        }

        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(STEAM_DRAIN)[v][j];
            int separatorIndexOfListConsumption = listOfConsumptions.indexOf(separator.getConsumptionOfSteamDrain());

            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;

                if (element.getClass() == Heaters.class) {
                    coefficientMatrix[materialBalanceEquation][separatorIndexOfListConsumption] = relations;
                    coefficientMatrix[heatBalanceEquation][separatorIndexOfListConsumption] = relations * separator.getEnthalpyOfSteamDrain();
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void matrixCompilationForCondenser(int v, Matrices matrices, Condenser condenser) {
        double[][] coefficientMatrix = matrices.coefficientMatrix;
        double[] freeMemoryMatrix = matrices.freeMemoryMatrix;
        ArrayList<Consumptions> listOfConsumptions = matrices.getListOfConsumptions();
        int materialBalanceEquation = matrices.getListOfEquation().indexOf(condenser.getMaterialBalanceEquation());
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];
            int condenserIndexOfListConsumption = listOfConsumptions.indexOf(condenser.getConsumptionOfHeatingSteam());
            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;

                if (element.getClass() == TurbineCylinders.class) {
                    coefficientMatrix[materialBalanceEquation][condenserIndexOfListConsumption] = relations;
                }
            }
        }

        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(STEAM_DRAIN)[v][j];
            int condenserIndexOfListConsumption = listOfConsumptions.indexOf(condenser.getConsumptionOfSteamDrain());
            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;

                if (element.getClass() == Heaters.class) {
                    if (relations == -1) {
                        coefficientMatrix[materialBalanceEquation][condenserIndexOfListConsumption] = relations;
                    }

                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void matrixCompilationForSuperheaters(int v, Matrices matrices, Superheaters superheater) {
        double[][] coefficientMatrix = matrices.coefficientMatrix;
        double[] freeMemoryMatrix = matrices.freeMemoryMatrix;
        ArrayList<Consumptions> listOfConsumptions = matrices.getListOfConsumptions();
        int materialBalanceEquationOnSteamDrainLine = matrices.getListOfEquation().indexOf(superheater.getMaterialBalanceEquationOnSteamDrainLine());
        int materialBalanceEquationOnHeatedMediumLine = matrices.getListOfEquation().indexOf(superheater.getMaterialBalanceEquationOnHeatedMediumLine());
        int heatBalanceEquation = matrices.getListOfEquation().indexOf(superheater.getHeatBalanceEquation());
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(SUPERHEATED_STEAM)[v][j];
            int superheaterIndexOfListConsumption = listOfConsumptions.indexOf(superheater.getConsumptionOfHeatedMedium());

            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;

                if (element.getClass() == Superheaters.class) {
                    Superheaters superheater2 = (Superheaters) element;
                    int indexOfListConsumption = listOfConsumptions.indexOf(superheater2.getConsumptionOfHeatedMedium());
                    if (relations == -1) {
                        coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][superheaterIndexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][superheaterIndexOfListConsumption] = relations * superheater.getEnthalpyOfHeatedMedium();
                    } else {
                        coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * superheater2.getEnthalpyOfHeatedMedium();
                    }
                }

                if (element.getClass() == TurbineCylinders.class) {
                    TurbineCylinders turbineCylinders = (TurbineCylinders) element;
                    coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][superheaterIndexOfListConsumption] = relations;
                    if (relations == 1) {
                        coefficientMatrix[heatBalanceEquation][superheaterIndexOfListConsumption] = relations * turbineCylinders
                                .parametersInSelection(turbineCylinders.NUMBER_OF_SELECTIONS + 1).getEnthalpy();
                    } else {
                        coefficientMatrix[heatBalanceEquation][superheaterIndexOfListConsumption] = relations * superheater.getEnthalpyOfHeatedMedium();
                    }
                }

                if (element.getClass() == Separator.class) {
                    Separator separator = (Separator) element;
                    int indexOfListConsumption = listOfConsumptions.indexOf(separator.getConsumptionOfHeatedMedium());
                    coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                    coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * separator.getEnthalpyOfHeatedMedium();
                }
            }
        }

        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];
            int superheaterIndexOfListConsumption = listOfConsumptions.indexOf(superheater.getConsumptionOfHeatingSteam());
            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;

                if (element.getClass() == TurbineCylinders.class) {
                    coefficientMatrix[materialBalanceEquationOnSteamDrainLine][superheaterIndexOfListConsumption] = relations;
                    coefficientMatrix[heatBalanceEquation][superheaterIndexOfListConsumption] = relations * superheater.getEnthalpyOfHeatingSteam() * superheater.getCoefficient();
                }
            }
        }
        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(STEAM_DRAIN)[v][j];
            int superheaterIndexOfListConsumption = listOfConsumptions.indexOf(superheater.getConsumptionOfSteamDrain());
            if (relations == -1 || relations == 1) {
                Elements element = vertexList.get(j).element;

                if (element.getClass() == Heaters.class) {
                    coefficientMatrix[materialBalanceEquationOnSteamDrainLine][superheaterIndexOfListConsumption] = relations;
                    coefficientMatrix[heatBalanceEquation][superheaterIndexOfListConsumption] = relations * superheater.getEnthalpyOfSteamDrain() * superheater.getCoefficient();
                }
            }
        }
    }




    private void matrixCompilationForElement(int v, int j, int relations, Matrices matrices) {


    }


    public Map<Integer, int[][]> getAdjMat() {
        return adjMat;
    }
}








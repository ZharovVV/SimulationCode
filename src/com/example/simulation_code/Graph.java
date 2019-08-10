package com.example.simulation_code;

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
        adjMat.put(SUPERHEATED_STEAM,matrix);
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
        return null;
        //....
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

    public Map<Integer, int[][]> getAdjMat() {
        return adjMat;
    }
}








package com.example.simulation_code;

import com.example.simulation_code.Elements.*;

import java.util.ArrayList;


public class Matrices {
    public double[][] coefficientMatrix;
    public double[] freeMemoryMatrix;
    private ArrayList<Double> listOfConsumption;

    public Matrices(ArrayList<Vertex> vertexList) {
        listOfConsumption = new ArrayList<>();
        int i = 0;
        int j = 0;

        for (Vertex vertex : vertexList) {
            Elements element = vertex.element;
            if (element.getClass() == Heaters.class) {
                Heaters heater = (Heaters) element;
                if (heater.isSurfaceHeater()) {
                    i = i + 3;
                    j = j + 3;

                } else {
                    i = i + 2;
                    j = j + 2;
                }
            }

            if (element.getClass() == Superheaters.class) {
                i = i + 3;
                j = j + 3;
            }

            if (element.getClass() == Separator.class) {
                i = i + 2;
                j = j + 3;
            }

            if (element.getClass() == TurbineCylinders.class) {
                i = i + 1;
            }

            if (element.getClass() == Condenser.class) {
                i = i + 1;
                j = j + 2;
            }
        }

        /*System.out.println("Число уравнений: " + i + " Число неизвестных: " + j);*/

        coefficientMatrix = new double[i][j];
        freeMemoryMatrix = new double[i];

    }

}

package com.example.thermal_circuit_simulation.Elements;

import com.example.thermal_circuit_simulation.Graph.Graph;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Consumptions;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Equation;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.MatrixCompilation;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Matrices;
import com.example.thermal_circuit_simulation.Graph.Vertex;
import com.hummeling.if97.IF97;

import java.util.ArrayList;
import java.util.Map;

import static com.example.thermal_circuit_simulation.Graph.Graph.*;

public class TurbineCylinders extends Elements implements MatrixCompilation {
    public final int NUMBER_OF_SELECTIONS;                                                  // Число отборов в турбине
    private ArrayList<Parameters> listOfParametersInSelections;                             // Список параметров отбора, включая параметры на входе и выходе из цилиндра

    private Equation materialBalanceEquation = new Equation(this);

    public TurbineCylinders(String name, int numberOfSelections) {
        super(name);
        this.NUMBER_OF_SELECTIONS = numberOfSelections;
        listOfParametersInSelections = new ArrayList<>(numberOfSelections + 2);
    }

    //------------------------------------------------------------------------------------------------------------------
    //Метод добавляет параметры отбора
    // numberOfSelection = 0 - параметры на входе в цилиндр
    // numberOfSelection + 2 - параметры ны выходе из цилиндра
    public void addSelection(int selectionNumber, double pressure, double XorT) {
        listOfParametersInSelections.add(selectionNumber, new Parameters(pressure, XorT));
    }

    public Parameters parametersInSelection(int selectionNumber) {
        return listOfParametersInSelections.get(selectionNumber);
    }

    @Override
    public void describe() {
        super.describe();
        for (Parameters parameters : listOfParametersInSelections) {
            int i = listOfParametersInSelections.indexOf(parameters);
            if (i == 0) {
                System.out.println("Параметры на входе в цилиндр:");
                System.out.println("Давление: " + parameters.getPressure() + " ,МПа");
                System.out.println("Температура: " + parameters.getTemperature() + " ,℃");
                System.out.println("Степень сухости: " + parameters.getDegreeOfDryness());
                System.out.println("Энтальпия: " + parameters.getEnthalpy() + " ,кДж/кг");
                System.out.println();
            } else if (i == NUMBER_OF_SELECTIONS + 1) {
                System.out.println("Параметры на выходе из цилиндра:");
                System.out.println("Давление: " + parameters.getPressure() + " ,МПа");
                System.out.println("Температура: " + parameters.getTemperature() + " ,℃");
                System.out.println("Степень сухости: " + parameters.getDegreeOfDryness());
                System.out.println("Энтальпия: " + parameters.getEnthalpy() + " ,кДж/кг");
                System.out.println();
            } else {
                System.out.println("Номер отбора:" + i);
                System.out.println("Давление: " + parameters.getPressure() + " ,МПа");
                System.out.println("Температура: " + parameters.getTemperature() + " ,℃");
                System.out.println("Степень сухости: " + parameters.getDegreeOfDryness());
                System.out.println("Энтальпия: " + parameters.getEnthalpy() + " ,кДж/кг");
                System.out.println();
            }
        }
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println();
    }

    public Equation getMaterialBalanceEquation() {
        return materialBalanceEquation;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void matrixCompilation(int v, Matrices matrices, Graph theGraph) {
        //--------------------------Инициализация-----------------------------------------------------------------------
        int nVerts = theGraph.getnVerts();
        Map<Integer, int[][]> adjMat = theGraph.getAdjMat();
        ArrayList<Vertex> vertexList = theGraph.getVertexList();

        double[][] coefficientMatrix = matrices.coefficientMatrix;
        double[] freeMemoryMatrix = matrices.freeMemoryMatrix;
        ArrayList<Consumptions> listOfConsumptions = matrices.getListOfColumnsOfConsumptions();

        // Получение номера строки в матрицах для цилиндра турбины
        int indexOfListOfEquation = matrices.getListOfLinesOfEquations().indexOf(this.getMaterialBalanceEquation());
        //--------------------------------------------------------------------------------------------------------------

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
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(superheater.getConsumptionOfHeatedMedium());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == Heaters.class) {
                    Heaters heater = (Heaters) element;
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == Deaerator.class) {
                    Deaerator deaerator = (Deaerator) element;
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(deaerator.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == Separator.class) {
                    if (relations == -1) {
                        Separator separator = (Separator) element;
                        // Получение номера столбца
                        int indexOfListConsumption = listOfConsumptions.indexOf(separator.getConsumptionOfHeatingSteam());
                        coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                    }
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
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(superheater.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == Heaters.class) {
                    Heaters heater = (Heaters) element;
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == Deaerator.class) {
                    Deaerator deaerator = (Deaerator) element;
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(deaerator.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == Separator.class) {
                    Separator separator = (Separator) element;
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(separator.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element.getClass() == Condenser.class) {
                    Condenser condenser = (Condenser) element;
                    // Получение номера столбца
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

    public static class Parameters {
        private double pressure;
        private double temperature;
        private double degreeOfDryness;
        private double enthalpy;

        public Parameters(double pressure, double temperatureOrDegreeOfDryness) {
            this.pressure = pressure;
            IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
            if (temperatureOrDegreeOfDryness > 1) {
                this.temperature = temperatureOrDegreeOfDryness;
                this.degreeOfDryness = Double.NaN;
                this.enthalpy = waterSteam.specificEnthalpyPT(pressure, temperatureOrDegreeOfDryness + 273.15);
            } else {
                this.temperature = Double.NaN;
                this.degreeOfDryness = temperatureOrDegreeOfDryness;
                this.enthalpy = waterSteam.specificEnthalpyPX(pressure, temperatureOrDegreeOfDryness);
            }
        }

        public double getPressure() {
            return pressure;
        }

        public double getEnthalpy() {
            return enthalpy;
        }

        public double getTemperature() {
            return temperature;
        }

        public double getDegreeOfDryness() {
            return degreeOfDryness;
        }
    }
}

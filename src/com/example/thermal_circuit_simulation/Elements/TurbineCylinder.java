package com.example.thermal_circuit_simulation.Elements;

import com.example.thermal_circuit_simulation.Elements.Seals.ValveStemSeals;
import com.example.thermal_circuit_simulation.Graph.*;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.*;
import com.example.thermal_circuit_simulation.ThermalEfficiencyIndicators.ThermalEfficiencyIndicators;
import com.hummeling.if97.IF97;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.thermal_circuit_simulation.Graph.Graph.*;

public class TurbineCylinder extends Element {
    public final int NUMBER_OF_SELECTIONS;                                                  // Число отборов в турбине
    private List<Parameters> listOfParametersInSelections;                             // Список параметров отбора, включая параметры на входе и выходе из цилиндра

    private Equation materialBalanceEquation = new Equation(this);

    private List<Consumptions> listOfConsumptionThroughTheCompartmentOfThisTurbine;

    public TurbineCylinder(String name, int numberOfSelections) {
        super(name);
        this.NUMBER_OF_SELECTIONS = numberOfSelections;
        listOfParametersInSelections = new ArrayList<>(numberOfSelections + 2);
        listOfConsumptionThroughTheCompartmentOfThisTurbine = new ArrayList<>();
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
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    public Equation getMaterialBalanceEquation() {
        return materialBalanceEquation;
    }

    @Override
    public void matrixCompilation(int v, Matrices matrices, Graph theGraph) {
        //--------------------------Инициализация-----------------------------------------------------------------------
        int nVerts = theGraph.getnVerts();
        Map<Integer, int[][]> adjMat = theGraph.getAdjMat();
        List<Vertex> vertexList = theGraph.getVertexList();

        double[][] coefficientMatrix = matrices.coefficientMatrix;
        double[] freeMemoryMatrix = matrices.freeMemoryMatrix;
        List<Consumptions> listOfConsumptions = matrices.getListOfColumnsOfConsumptions();

        // Получение номера строки в матрицах для цилиндра турбины
        int indexOfListOfEquation = matrices.getListOfLinesOfEquations().indexOf(this.getMaterialBalanceEquation());
        //--------------------------------------------------------------------------------------------------------------

        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(SUPERHEATED_STEAM)[v][j];

            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;
                if (element instanceof SteamGenerator) {
                    SteamGenerator steamGenerator = (SteamGenerator) element;
                    freeMemoryMatrix[indexOfListOfEquation] += (-1) * relations * steamGenerator.getSteamConsumption();
                }

                if (element instanceof Superheater) {
                    Superheater superheater = (Superheater) element;
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(superheater.getConsumptionOfHeatedMedium());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element instanceof Heater) {
                    Heater heater = (Heater) element;
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element instanceof Deaerator) {
                    Deaerator deaerator = (Deaerator) element;
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(deaerator.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element instanceof Separator) {
                    if (relations == -1) {
                        Separator separator = (Separator) element;
                        // Получение номера столбца
                        int indexOfListConsumption = listOfConsumptions.indexOf(separator.getConsumptionOfHeatingSteam());
                        coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                    }
                }

                if (element instanceof TurboDrive) {
                    TurboDrive turboDrive = (TurboDrive) element;
                    freeMemoryMatrix[indexOfListOfEquation] += (-1) * relations * turboDrive.getSteamConsumption();
                }
            }
        }

        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];

            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;
                if (element instanceof SteamGenerator) {
                    SteamGenerator steamGenerator = (SteamGenerator) element;
                    freeMemoryMatrix[indexOfListOfEquation] += (-1) * relations * steamGenerator.getSteamConsumption();
                }

                if (element instanceof Superheater) {
                    Superheater superheater = (Superheater) element;
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(superheater.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element instanceof Heater) {
                    Heater heater = (Heater) element;
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element instanceof Deaerator) {
                    Deaerator deaerator = (Deaerator) element;
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(deaerator.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element instanceof Separator) {
                    Separator separator = (Separator) element;
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(separator.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element instanceof Condenser) {
                    Condenser condenser = (Condenser) element;
                    // Получение номера столбца
                    int indexOfListConsumption = listOfConsumptions.indexOf(condenser.getConsumptionOfHeatingSteam());
                    coefficientMatrix[indexOfListOfEquation][indexOfListConsumption] = relations;
                }

                if (element instanceof TurboDrive) {
                    TurboDrive turboDrive = (TurboDrive) element;
                    freeMemoryMatrix[indexOfListOfEquation] += (-1) * relations * turboDrive.getSteamConsumption();
                }
            }
        }
    }

    @Override
    public void calculationOfThermalEfficiencyIndicators(int v, ThermalEfficiencyIndicators thermalEfficiencyIndicators, Graph theGraph) {
        //--------------------------Инициализация-----------------------------------------------------------------------
        int nVerts = theGraph.getnVerts();
        Map<Integer, int[][]> adjMat = theGraph.getAdjMat();
        List<Vertex> vertexList = theGraph.getVertexList();
        ArrayList<Consumptions> listOfConsumptionThroughTheCompartment = thermalEfficiencyIndicators.getListOfConsumptionThroughTheCompartment();
        ArrayList<Double> listOfHeatTransferCompartments = thermalEfficiencyIndicators.getListOfHeatTransferCompartments();


        for (Parameters parametersInSelection : listOfParametersInSelections) {
            int index = listOfParametersInSelections.indexOf(parametersInSelection);
            if (index < listOfParametersInSelections.size() - 1) {
                double heatTransfer = parametersInSelection.enthalpy - listOfParametersInSelections.get(index + 1).enthalpy;
                if (heatTransfer != 0.0) {
                    Consumptions consumption = new Consumptions();
                    listOfConsumptionThroughTheCompartmentOfThisTurbine.add(consumption);
                    int i = listOfConsumptionThroughTheCompartmentOfThisTurbine.indexOf(consumption);
                    listOfConsumptionThroughTheCompartment.add(listOfConsumptionThroughTheCompartmentOfThisTurbine.get(i));
                    listOfHeatTransferCompartments.add(heatTransfer);
                }
            }
        }


        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(SUPERHEATED_STEAM)[v][j];
            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;

                if (element instanceof Superheater) {
                    Superheater superheater = (Superheater) element;
                    listOfConsumptionThroughTheCompartmentOfThisTurbine.get(0).consumptionValue += relations * superheater.getConsumptionOfHeatedMedium().consumptionValue;
                }

                if (element instanceof TurboDrive) {
                    TurboDrive turboDrive = (TurboDrive) element;
                    listOfConsumptionThroughTheCompartmentOfThisTurbine.get(0).consumptionValue += relations * turboDrive.getSteamConsumption();
                }
            }
        }

        for (int j = 0; j < nVerts; j++) {
            int relations = adjMat.get(HEATING_STEAM)[v][j];
            if (relations == -1 || relations == 1) {
                Element element = vertexList.get(j).element;

                if (element instanceof SteamGenerator) {
                    SteamGenerator steamGenerator = (SteamGenerator) element;
                    listOfConsumptionThroughTheCompartmentOfThisTurbine.get(0).consumptionValue += relations * steamGenerator.getSteamConsumption();
                }

                if (element instanceof ValveStemSeals) {
                    ValveStemSeals valveStemSeal = (ValveStemSeals) element;
                    double sealConsumption = valveStemSeal.getElementContributionToSteamConsumptionInSeals().get(this);
                    listOfConsumptionThroughTheCompartmentOfThisTurbine.get(0).consumptionValue += relations * sealConsumption;
                }

                if (element instanceof TurboDrive) {
                    TurboDrive turboDrive = (TurboDrive) element;
                    int index = turboDrive.getSelectionNumber();
                    listOfConsumptionThroughTheCompartmentOfThisTurbine.get(index).consumptionValue += relations * turboDrive.getSteamConsumption();
                }

                if (element instanceof Superheater) {
                    Superheater superheater = (Superheater) element;
                    int selectionNumber = superheater.getSelectionNumber();
                    listOfConsumptionThroughTheCompartmentOfThisTurbine.get(selectionNumber).consumptionValue +=
                            relations * superheater.getConsumptionOfHeatingSteam().consumptionValue;
                }

                if (element instanceof Heater) {
                    Heater heater = (Heater) element;
                    int selectionNumber = heater.getSelectionNumber();
                    if (selectionNumber < listOfConsumptionThroughTheCompartmentOfThisTurbine.size()) {
                        listOfConsumptionThroughTheCompartmentOfThisTurbine.get(selectionNumber).consumptionValue +=
                                relations * heater.getConsumptionOfHeatingSteam().consumptionValue;
                    }
                }

            }
        }

        for (Consumptions consumptions : listOfConsumptionThroughTheCompartmentOfThisTurbine) {
            int index = listOfConsumptionThroughTheCompartmentOfThisTurbine.indexOf(consumptions);
            if (index > 0) {
                consumptions.consumptionValue += listOfConsumptionThroughTheCompartmentOfThisTurbine.get(index - 1).consumptionValue;
            }
        }
    }

    public static class Parameters {
        private double pressure;
        private double temperature;
        private double degreeOfDryness;
        private double enthalpy;

        Parameters(double pressure, double temperatureOrDegreeOfDryness) {
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

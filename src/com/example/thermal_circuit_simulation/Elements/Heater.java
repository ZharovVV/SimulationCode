package com.example.thermal_circuit_simulation.Elements;

import com.example.thermal_circuit_simulation.Graph.Graph;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Consumptions;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Equation;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.MatrixCompilation;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Matrices;
import com.example.thermal_circuit_simulation.Graph.Vertex;
import com.hummeling.if97.IF97;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.Map;

import static com.example.thermal_circuit_simulation.Graph.Graph.*;

public class Heater extends Element implements MatrixCompilation {
    //-----------------------------Характеристики подогревателя---------------------------------------------------------
    private int heaterNumber;                                   // Номер подогревателя по ходу воды
    private boolean isSurfaceHeater;                            // Подогреватель поверхностного типа? false, если тип подогревателя смешивающий
    private int selectionNumber;                                // Номер отбора
    private double hydraulicResistanceFromSelectionToHeater;    // Гидравлическое сопротивление от отбора до подогревателя
    private double hydraulicResistanceInHeater;                 // Гидравлическое сопротивление в подогревателе
    private double underheatingOfSteamDrain;                    // Недогрев дренажа (температурный напор между обогреваемой средой на входе и дренажом на выходе)
    private double underheatingOfHeatedMedium;                  // Недогрев обогреваемой среды на выходе до температуры в подогревателе
    private double coefficient;                                           // Коэффициент, учитывающий тепловые потери
    //-----------------------------Характеристики греющего пара---------------------------------------------------------
    private double pressureOfHeatingSteam;                      // Давление греющего пара на входе в подогреватель
    private double temperatureOfHeatingSteam;                   // Температура греющего пара на входе в подогреватель
    private double enthalpyOfHeatingSteam;                      // Энтальпия греющего пара на входе в подогреватель
    private Consumptions consumptionOfHeatingSteam = new Consumptions();                      // Расход греющего пара на входе в подогреватель

    //-----------------------------Характеристики дренажа пара----------------------------------------------------------
    private double pressureOfSteamDrain;                        // Давление дренажа пара на выходе из подогревателя
    private double temperatureOfSteamDrain;                     // Температура дренажа пара на выходе из подогревателя
    private double enthalpyOfSteamDrain;                        // Энтальпия дренажа пара на выходе из подогревателя
    private Consumptions consumptionOfSteamDrain = new Consumptions();                        // Расход дренажа пара на выходе из подогревателя
    //-----------------------------Характеристики обогреваемой среды на выходе------------------------------------------
    private double pressureOfHeatedMedium;                      // Давление обогреваемой среды на выходе из подогревателя
    private double temperatureOfHeatedMedium;                   // Температура обогреваемой среды на выходе из подогревателя
    private double enthalpyOfHeatedMedium;                      // Энтальпия обогреваемой среды на выходе из подогревателя
    private Consumptions consumptionOfHeatedMedium = new Consumptions();                      // Расход обогреваемой среды на выходе из подогревателя

    private Equation materialBalanceEquationOnSteamDrainLine = new Equation(this);
    private Equation materialBalanceEquationOnHeatedMediumLine = new Equation(this);
    private Equation heatBalanceEquation = new Equation(this);


    //-----------------------------Конструктор для поверхностного подогревателя-----------------------------------------
    public Heater(
            String name,                                        // Название подогревателя
            int heaterNumber,
            double hydraulicResistanceInHeater,
            double underheatingOfSteamDrain,
            double underheatingOfHeatedMedium
    ) {
        super(name);
        this.heaterNumber = heaterNumber;
        this.isSurfaceHeater = true;
        this.hydraulicResistanceInHeater = hydraulicResistanceInHeater;
        this.underheatingOfSteamDrain = underheatingOfSteamDrain;
        this.underheatingOfHeatedMedium = underheatingOfHeatedMedium;
    }

    //-----------------------------Конструктор для смешивающего подогревателя-------------------------------------------
    public Heater(
            String name,
            double pressureInHeater,
            int selectionNumber,
            TurbineCylinder turbineCylinder,
            Element previousElement
    ) {
        super(name);
        this.isSurfaceHeater = false;
        this.selectionNumber = selectionNumber;
        this.pressureOfHeatingSteam = pressureInHeater;
        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
        this.temperatureOfHeatingSteam = waterSteam.saturationTemperatureP(pressureInHeater) - 273.15;
        this.enthalpyOfHeatingSteam = turbineCylinder.parametersInSelection(selectionNumber).getEnthalpy();
        this.pressureOfHeatedMedium = pressureInHeater;
        this.temperatureOfHeatedMedium = temperatureOfHeatingSteam;
        this.enthalpyOfHeatedMedium = waterSteam.specificEnthalpySaturatedLiquidP(pressureInHeater);
        // TODO: 23.08.2019 Переделать конструктор для смешивающего подогревателя

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void calculationOfInitialParameters(int v, Graph theGraph) {
        //--------------------------Инициализация-----------------------------------------------------------------------
        int nVerts = theGraph.getnVerts();
        Map<Integer, int[][]> adjMat = theGraph.getAdjMat();
        ArrayList<Vertex> vertexList = theGraph.getVertexList();
        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
        if (!isSurfaceHeater) {
            // TODO: 09.09.2019 Расчет начальных параметров для смешивающего подогревателя
        } else {
            //--------------------------------Связи с элементами по линии греющего пара---------------------------------
            for (int j = 0; j < nVerts; j++) {
                int relations = adjMat.get(HEATING_STEAM)[v][j];
                if (relations == 1) {
                    Element element = vertexList.get(j).element;

                    if (element.getClass() == TurbineCylinder.class) {
                        TurbineCylinder turbineCylinder = (TurbineCylinder) element;
                        if (!(heaterNumber == 0)) {
                            this.hydraulicResistanceFromSelectionToHeater =         // Считаем потери в труб-де от отбора до подогревателя
                                    turbineCylinder.parametersInSelection(selectionNumber).getPressure() * (11 - heaterNumber) / 100;
                        }

                        this.pressureOfHeatingSteam =                           // Считаем давление в подогревателе
                                turbineCylinder.parametersInSelection(selectionNumber).getPressure() - hydraulicResistanceFromSelectionToHeater;
                        this.pressureOfSteamDrain = pressureOfHeatingSteam;

                        this.temperatureOfHeatingSteam =                        // Температура греющего пара
                                waterSteam.saturationTemperatureP(pressureOfHeatingSteam) - 273.15;

                        this.enthalpyOfHeatingSteam =                           // Энтальпия греющего пара
                                turbineCylinder.parametersInSelection(selectionNumber).getEnthalpy();
                    }
                }
            }
            //----------------------------------------------------------------------------------------------------------

            //--------------------------------Связи с элементами по линии питательной воды------------------------------
            for (int j = 0; j < nVerts; j++) {
                int relations = adjMat.get(FEED_WATER)[v][j];
                if (relations == 1) {
                    Element element = vertexList.get(j).element;

                    if (element.getClass() == Pump.class) {
                        Pump pump = (Pump) element;
                        this.pressureOfHeatedMedium =                                           // Давление обогреваемой среды на выходе = давлению обогреваемой среды на выходе из насоса - потери в подогревателе
                                pump.getOutletPressure() - hydraulicResistanceInHeater;
                        if (Double.isNaN(underheatingOfSteamDrain)) {                           // Если охладитель дренажа отсутствует
                            this.temperatureOfSteamDrain = temperatureOfHeatingSteam;               // Температура дренажа = температуре греющего пара
                        } else {                                                                // иначе
                            this.temperatureOfSteamDrain =                                          // Температура дренажа = температуре обогреваемой среды на выходе из насоса + недогрев
                                    pump.getOutletTemperature() + underheatingOfSteamDrain;
                        }
                    }

                    if (element.getClass() == Heater.class) {
                        Heater heater = (Heater) element;
                        this.pressureOfHeatedMedium =                                           // Давление обогреваемой среды на выходе = давлению обогреваемой среды на выходе из предыдущего элемента - потери в подогревателе
                                heater.getPressureOfHeatedMedium() - hydraulicResistanceInHeater;
                        if (Double.isNaN(underheatingOfSteamDrain)) {                           // Если охладитель дренажа отсутствует
                            this.temperatureOfSteamDrain = temperatureOfHeatingSteam;               // Температура дренажа = температуре греющего пара
                        } else {                                                                // иначе
                            this.temperatureOfSteamDrain =                                          // Температура дренажа = температуре обогреваемой среды на выходе из предыдущего подогревателя + недогрев
                                    heater.getTemperatureOfHeatedMedium() + underheatingOfSteamDrain;
                        }
                    }

                    if (element.getClass() == MixingPoint.class) {
                        MixingPoint mixingPoint = (MixingPoint) element;
                        this.pressureOfHeatedMedium =                                           // Давление обогреваемой среды на выходе = давлению обогреваемой среды на выходе из предыдущего элемента - потери в подогревателе
                                mixingPoint.getPressureOfHeatedMedium() - hydraulicResistanceInHeater;
                        if (Double.isNaN(underheatingOfSteamDrain)) {                           // Если охладитель дренажа отсутствует
                            this.temperatureOfSteamDrain = temperatureOfHeatingSteam;               // Температура дренажа = температуре греющего пара
                        } else {                                                                // иначе
                            this.temperatureOfSteamDrain =                                          // Температура дренажа = температуре обогреваемой среды на выходе из предыдущего подогревателя + недогрев
                                    mixingPoint.getTemperatureOfHeatedMedium() + underheatingOfSteamDrain;
                        }
                    }

                }
            }
            //----------------------------------------------------------------------------------------------------------

            //--------------------------------Связи с элементами по линии сетевой воды----------------------------------
            for (int j = 0; j < nVerts; j++) {
                int relations = adjMat.get(NETWORK_WATER)[v][j];
                if (relations == 1) {
                    Element element = vertexList.get(j).element;

                    if (element.getClass() == Heater.class) {
                        Heater heater = (Heater) element;
                        this.pressureOfHeatedMedium =                                           // Давление обогреваемой среды на выходе = давлению обогреваемой среды на выходе из предыдущего элемента - потери в подогревателе
                                heater.getPressureOfHeatedMedium() - hydraulicResistanceInHeater;
                        if (Double.isNaN(underheatingOfSteamDrain)) {                           // Если охладитель дренажа отсутствует
                            this.temperatureOfSteamDrain = temperatureOfHeatingSteam;               // Температура дренажа = температуре греющего пара
                        } else {                                                                // иначе
                            this.temperatureOfSteamDrain =                                          // Температура дренажа = температуре обогреваемой среды на выходе из предыдущего подогревателя + недогрев
                                    heater.getTemperatureOfHeatedMedium() + underheatingOfSteamDrain;
                        }
                    }

                    if (element.getClass() == HeatNetwork.class) {
                        HeatNetwork heatNetwork = (HeatNetwork) element;
                        this.pressureOfHeatedMedium =                                           // Давление обогреваемой среды на выходе = давлению обогреваемой среды на выходе из ТС - потери в подогревателе
                                heatNetwork.getOutletPressure() - hydraulicResistanceInHeater;
                        if (Double.isNaN(underheatingOfSteamDrain)) {                           // Если охладитель дренажа отсутствует
                            this.temperatureOfSteamDrain = temperatureOfHeatingSteam;               // Температура дренажа = температуре греющего пара
                        } else {                                                                // иначе
                            this.temperatureOfSteamDrain =                                          // Температура дренажа = температуре обогреваемой среды на выходе из ТС + недогрев
                                    heatNetwork.getOutletTemperature() + underheatingOfSteamDrain;
                        }
                    }
                }
            }
            //----------------------------------------------------------------------------------------------------------

            this.enthalpyOfSteamDrain =                                             // Энтальпия дренажа находится по температуре дренажа на линии насыщения для воды
                    waterSteam.specificEnthalpySaturatedLiquidT(temperatureOfSteamDrain + 273.15);

            this.temperatureOfHeatedMedium = temperatureOfHeatingSteam - underheatingOfHeatedMedium;
            this.enthalpyOfHeatedMedium = waterSteam.specificEnthalpyPT(pressureOfHeatedMedium, temperatureOfHeatedMedium + 273.15);
            this.coefficient = 1 - heaterNumber / 1000;
        }
    }

    @Override
    public void setSelectionNumber(int selectionNumber) {
        this.selectionNumber = selectionNumber;
    }

    public double getTemperatureOfHeatedMedium() {
        return temperatureOfHeatedMedium;
    }

    public double getPressureOfHeatedMedium() {
        return pressureOfHeatedMedium;
    }

    public double getEnthalpyOfHeatedMedium() {
        return enthalpyOfHeatedMedium;
    }

    public double getEnthalpyOfHeatingSteam() {
        return enthalpyOfHeatingSteam;
    }

    public double getEnthalpyOfSteamDrain() {
        return enthalpyOfSteamDrain;
    }

    public double getPressureOfSteamDrain() {
        return pressureOfSteamDrain;
    }

    public double getTemperatureOfSteamDrain() {
        return temperatureOfSteamDrain;
    }

    public int getSelectionNumber() {
        return selectionNumber;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public Consumptions getConsumptionOfHeatingSteam() {
        return consumptionOfHeatingSteam;
    }

    public Consumptions getConsumptionOfSteamDrain() {
        return consumptionOfSteamDrain;
    }

    public Consumptions getConsumptionOfHeatedMedium() {
        return consumptionOfHeatedMedium;
    }

    public boolean isSurfaceHeater() {
        return isSurfaceHeater;
    }

    public Equation getMaterialBalanceEquationOnSteamDrainLine() {
        return materialBalanceEquationOnSteamDrainLine;
    }

    public Equation getMaterialBalanceEquationOnHeatedMediumLine() {
        return materialBalanceEquationOnHeatedMediumLine;
    }

    public Equation getHeatBalanceEquation() {
        return heatBalanceEquation;
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

        if (!isSurfaceHeater) {
            // TODO: 23.08.2019 Переделать метод для смешивающего подогревателя
            //----------------------Продолжение инициализации для смешивающего подогревателя----------------------------
            // Получение номера строки в матрице, в которую записывается уравнение материального баланса по линии обогреваемой среды для Подогревателя
            int materialBalanceEquationOnHeatedMediumLine = matrices.getListOfLinesOfEquations().indexOf(this.getMaterialBalanceEquationOnHeatedMediumLine());
            // Получение номера строки в матрице, в которую записывается уравнение теплового баланса для Подогревателя
            int heatBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(this.getHeatBalanceEquation());
            //----------------------------------------------------------------------------------------------------------

            //--------------------------------Связи с элементами по линии греющего пара---------------------------------
            for (int j = 0; j < nVerts; j++) {
                int relations = adjMat.get(HEATING_STEAM)[v][j];
                // Номер столбца расхода греющего пара Подогревателя
                int heaterIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfHeatingSteam());
                if (relations == -1 || relations == 1) {
                    Element element = vertexList.get(j).element;

                    if (element.getClass() == TurbineCylinder.class) {
                        coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][heaterIndexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfHeatingSteam();
                    }
                }
            }
            //----------------------------------------------------------------------------------------------------------

            //--------------------------------Связи с элементами по линии дренажа греющего пара-------------------------
            for (int j = 0; j < nVerts; j++) {
                int relations = adjMat.get(STEAM_DRAIN)[v][j];
                if (relations == 1) {
                    Element element = vertexList.get(j).element;

                    if (element.getClass() == Separator.class) {
                        Separator separator = (Separator) element;
                        // Номер столбца расхода дренажа греющего пара Сепаратора
                        int indexOfListConsumption = listOfConsumptions.indexOf(separator.getConsumptionOfSteamDrain());
                        coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * separator.getEnthalpyOfSteamDrain();
                    }

                    if (element.getClass() == Superheater.class) {
                        Superheater superheater = (Superheater) element;
                        // Номер столбца расхода дренажа греющего пара Пароперегревателя
                        int indexOfListConsumption = listOfConsumptions.indexOf(superheater.getConsumptionOfSteamDrain());
                        coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * superheater.getEnthalpyOfSteamDrain();
                    }

                    if (element.getClass() == Heater.class) {
                        Heater heater = (Heater) element;
                        // Номер столбца расхода дренажа греющего пара Подогревателя
                        int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfSteamDrain());
                        coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * heater.getEnthalpyOfSteamDrain();
                    }
                }
            }
            //----------------------------------------------------------------------------------------------------------

            //--------------------------------Связи с элементами по линии питательной воды------------------------------
            for (int j = 0; j < nVerts; j++) {
                int relations = adjMat.get(FEED_WATER)[v][j];
                // Номер столбца расхода обогреваемой среды Подогревателя
                int heaterIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfHeatedMedium());
                if (relations == 1 || relations == -1) {
                    Element element = vertexList.get(j).element;

                    if (element.getClass() == Pump.class) {
                        if (relations == -1) {
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][heaterIndexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfHeatedMedium();
                        } else {
                            Pump pump = (Pump) element;
                            // Номер столбца расхода воды Насоса
                            int indexOfListConsumption = listOfConsumptions.indexOf(pump.getConsumptionOfWater());
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * pump.getOutletEnthalpy();
                        }
                    }

                    if (element.getClass() == Heater.class) {
                        if (relations == -1) {
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][heaterIndexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfHeatedMedium();
                        } else {
                            Heater heater = (Heater) element;
                            // Номер столбца расхода обогреваемой среды Подогревателя
                            int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfHeatedMedium());
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * heater.getEnthalpyOfHeatedMedium();
                        }
                    }
                }
            }
            //----------------------------------------------------------------------------------------------------------


        } else {
            //----------------------Продолжение инициализации для поверхностного подогревателя--------------------------
            // Получение номера строки в матрице, в которую записывается уравнение материального баланса по линии дренажа пара для Подогревателя
            int materialBalanceEquationOnSteamDrainLine = matrices.getListOfLinesOfEquations().indexOf(this.getMaterialBalanceEquationOnSteamDrainLine());
            // Получение номера строки в матрице, в которую записывается уравнение материального баланса по линии обогреваемой среды для Подогревателя
            int materialBalanceEquationOnHeatedMediumLine = matrices.getListOfLinesOfEquations().indexOf(this.getMaterialBalanceEquationOnHeatedMediumLine());
            // Получение номера строки в матрице, в которую записывается уравнение теплового баланса для Подогревателя
            int heatBalanceEquation = matrices.getListOfLinesOfEquations().indexOf(this.getHeatBalanceEquation());
            //----------------------------------------------------------------------------------------------------------

            //--------------------------------Связи с элементами по линии греющего пара---------------------------------
            for (int j = 0; j < nVerts; j++) {
                int relations = adjMat.get(HEATING_STEAM)[v][j];
                // Номер столбца расхода греющего пара Подогревателя
                int heaterIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfHeatingSteam());
                if (relations == -1 || relations == 1) {
                    Element element = vertexList.get(j).element;

                    if (element.getClass() == TurbineCylinder.class) {
                        coefficientMatrix[materialBalanceEquationOnSteamDrainLine][heaterIndexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfHeatingSteam();
                    }
                }
            }
            //----------------------------------------------------------------------------------------------------------

            //--------------------------------Связи с элементами по линии дренажа греющего пара-------------------------
            for (int j = 0; j < nVerts; j++) {
                int relations = adjMat.get(STEAM_DRAIN)[v][j];
                // Номер столбца расхода дренажа греющего пара Подогревателя
                int heaterIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfSteamDrain());
                if (relations == -1 || relations == 1) {
                    Element element = vertexList.get(j).element;

                    if (element.getClass() == Separator.class) {
                        Separator separator = (Separator) element;
                        // Номер столбца расхода дренажа Сепаратора
                        int indexOfListConsumption = listOfConsumptions.indexOf(separator.getConsumptionOfSteamDrain());
                        coefficientMatrix[materialBalanceEquationOnSteamDrainLine][indexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * separator.getEnthalpyOfSteamDrain() * this.coefficient;
                    }

                    if (element.getClass() == Superheater.class) {
                        Superheater superheater = (Superheater) element;
                        // Номер столбца расхода дренажа Пароперегревателя
                        int indexOfListConsumption = listOfConsumptions.indexOf(superheater.getConsumptionOfSteamDrain());
                        coefficientMatrix[materialBalanceEquationOnSteamDrainLine][indexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * superheater.getEnthalpyOfSteamDrain() * this.coefficient;
                    }

                    if (element.getClass() == Heater.class) {
                        if (relations == -1) {
                            coefficientMatrix[materialBalanceEquationOnSteamDrainLine][heaterIndexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfSteamDrain() * this.coefficient;
                        } else {
                            Heater heater = (Heater) element;
                            // Номер столбца расхода дренажа другого подогревателя
                            int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfSteamDrain());
                            coefficientMatrix[materialBalanceEquationOnSteamDrainLine][indexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * heater.getEnthalpyOfSteamDrain() * this.coefficient;
                        }
                    }

                    if (element.getClass() == Deaerator.class) {
                        if (relations == -1) {
                            coefficientMatrix[materialBalanceEquationOnSteamDrainLine][heaterIndexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfSteamDrain() * this.coefficient;
                        }
                    }

                    if (element.getClass() == Condenser.class) {
                        coefficientMatrix[materialBalanceEquationOnSteamDrainLine][heaterIndexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfSteamDrain() * this.coefficient;
                    }

                    if (element.getClass() == Pump.class) {
                        coefficientMatrix[materialBalanceEquationOnSteamDrainLine][heaterIndexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfSteamDrain() * this.coefficient;
                    }
                }
            }
            //----------------------------------------------------------------------------------------------------------

            //--------------------------------Связи с элементами по линии питательной воды------------------------------
            for (int j = 0; j < nVerts; j++) {
                int relations = adjMat.get(FEED_WATER)[v][j];
                // Номер столбца расхода обогреваемой среды Подогревателя
                int heaterIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfHeatedMedium());
                if (relations == -1 || relations == 1) {
                    Element element = vertexList.get(j).element;

                    if (element.getClass() == Pump.class) {
                        if (relations == -1) {
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][heaterIndexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfHeatedMedium();
                        } else {
                            Pump pump = (Pump) element;
                            int indexOfListConsumption = listOfConsumptions.indexOf(pump.getConsumptionOfWater());
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * pump.getOutletEnthalpy();
                        }
                    }

                    if (element.getClass() == Heater.class) {
                        if (relations == -1) {
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][heaterIndexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfHeatedMedium();
                        } else {
                            Heater heater = (Heater) element;
                            int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfHeatedMedium());
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * heater.getEnthalpyOfHeatedMedium();
                        }
                    }

                    if (element.getClass() == Deaerator.class) {
                        if (relations == -1) {
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][heaterIndexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfHeatedMedium();
                        } else {
                            Deaerator deaerator = (Deaerator) element;
                            int indexOfListConsumption = listOfConsumptions.indexOf(deaerator.getConsumptionOfHeatedMedium());
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * deaerator.getEnthalpyOfHeatedMedium();
                        }
                    }

                    if (element.getClass() == SteamGenerator.class) {
                        coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][heaterIndexOfListConsumption] = relations;
                        coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfHeatedMedium();
                    }

                    if (element.getClass() == MixingPoint.class) {
                        if (relations == -1) {
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][heaterIndexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfHeatedMedium();
                        } else {
                            MixingPoint mixingPoint = (MixingPoint) element;
                            int indexOfListConsumption = listOfConsumptions.indexOf(mixingPoint.getConsumptionOfHeatedMedium());
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * mixingPoint.getEnthalpyOfHeatedMedium();
                        }
                    }
                }
            }
            //----------------------------------------------------------------------------------------------------------

            //--------------------------------Связи с элементами по линии сетевой воды----------------------------------
            for (int j = 0; j < nVerts; j++) {
                int relations = adjMat.get(NETWORK_WATER)[v][j];
                // Номер столбца расхода обогреваемой среды Подогревателя
                int heaterIndexOfListConsumption = listOfConsumptions.indexOf(this.getConsumptionOfHeatedMedium());
                if (relations == -1 || relations == 1) {
                    Element element = vertexList.get(j).element;

                    if (element.getClass() == Heater.class) {
                        if (relations == -1) {
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][heaterIndexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfHeatedMedium();
                        } else {
                            Heater heater = (Heater) element;
                            // Номер столбца расхода обогреваемой среды другого подогревателя
                            int indexOfListConsumption = listOfConsumptions.indexOf(heater.getConsumptionOfHeatedMedium());
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][indexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][indexOfListConsumption] = relations * heater.getEnthalpyOfHeatedMedium();
                        }
                    }

                    if (element.getClass() == HeatNetwork.class) {
                        if (relations == -1) {
                            coefficientMatrix[materialBalanceEquationOnHeatedMediumLine][heaterIndexOfListConsumption] = relations;
                            coefficientMatrix[heatBalanceEquation][heaterIndexOfListConsumption] = relations * this.getEnthalpyOfHeatedMedium();
                        } else {
                            HeatNetwork heatNetwork = (HeatNetwork) element;
                            freeMemoryMatrix[materialBalanceEquationOnHeatedMediumLine] = (-1) * relations * heatNetwork.getNetworkWaterConsumption();
                            freeMemoryMatrix[heatBalanceEquation] = (-1) * relations * heatNetwork.getNetworkWaterConsumption() * heatNetwork.getOutletEnthalpy();
                        }
                    }
                }
            }
            //----------------------------------------------------------------------------------------------------------


        }
    }

    @Override
    public void describe() {
        super.describe();
        System.out.println("-----------------------------Характеристики подогревателя---------------------------------------------------------");
        System.out.println("Номер подогревателя по ходу воды: " + heaterNumber);
        System.out.print("Тип подогревателя: ");
        if (isSurfaceHeater) {
            System.out.println("поверхностный");
        } else {
            System.out.println("смеешивающий");
        }
        System.out.println("Гидравлическое сопротивление от отбора до подогревателя: " + hydraulicResistanceFromSelectionToHeater + " ,МПа");
        System.out.println("Гидравлическое сопротивление в подогревателе: " + hydraulicResistanceInHeater + " ,МПа");
        System.out.println("Недогрев дренажа (температурный напор между обогреваемой средой на входе и дренажом на выходе): " + underheatingOfSteamDrain + " ,℃");
        System.out.println("Недогрев обогреваемой среды на выходе до температуры в подогревателе: " + underheatingOfHeatedMedium + " ,℃");
        System.out.println("-----------------------------Характеристики греющего пара---------------------------------------------------------");
        System.out.println("Давление греющего пара на входе в подогреватель: " + pressureOfHeatingSteam + " ,МПа");
        System.out.println("Температура греющего пара на входе в подогреватель: " + temperatureOfHeatingSteam + " ,℃");
        System.out.println("Энтальпия греющего пара на входе в подогреватель: " + enthalpyOfHeatingSteam + " ,кДж/кг");
        System.out.println("Расход греющего пара: " + consumptionOfHeatingSteam.consumptionValue + " ,кг/c");
        System.out.println("-----------------------------Характеристики дренажа пара----------------------------------------------------------");
        System.out.println("Давление дренажа пара на выходе из подогревателя: " + pressureOfSteamDrain + " ,МПа");
        System.out.println("Температура дренажа пара на выходе из подогревателя: " + temperatureOfSteamDrain + " ,℃");
        System.out.println("Энтальпия дренажа пара на выходе из подогревателя: " + enthalpyOfSteamDrain + " ,кДж/кг");
        System.out.println("Расход дренажа пара на выходе из подогревателя: " + consumptionOfSteamDrain.consumptionValue + " ,кг/c");
        System.out.println("-----------------------------Характеристики обогреваемой среды на выходе------------------------------------------");
        System.out.println("Давление обогреваемой среды на выходе из подогревателя: " + pressureOfHeatedMedium + " ,МПа");
        System.out.println("Температура обогреваемой среды на выходе из подогревателя: " + temperatureOfHeatedMedium + " ,℃");
        System.out.println("Энтальпия обогреваемой среды на выходе из подогревателя: " + enthalpyOfHeatedMedium + " ,кДж/кг");
        System.out.println("Расход обогреваемой среды на выходе из подогревателя: " + consumptionOfHeatedMedium.consumptionValue + " ,кг/c");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }
}

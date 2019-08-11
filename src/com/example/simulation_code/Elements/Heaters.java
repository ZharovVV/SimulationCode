package com.example.simulation_code.Elements;

import com.example.simulation_code.HelperСlasses.Consumptions;
import com.example.simulation_code.HelperСlasses.Equation;
import com.hummeling.if97.IF97;

public class Heaters extends Elements {
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

    private Equation materialBalanceEquationOnSteamDrainLine = new Equation();
    private Equation materialBalanceEquationOnHeatedMediumLine = new Equation();
    private Equation heatBalanceEquation = new Equation();



    //-----------------------------Конструктор для поверхностного подогревателя-----------------------------------------
    public Heaters(
            String name,                                        // Название подогревателя
            int heaterNumber,
            double hydraulicResistanceInHeater,
            double underheatingOfSteamDrain,
            double underheatingOfHeatedMedium,
            int selectionNumber,
            TurbineCylinders turbineCylinder,                   // Цилиндр турбины
            Elements previousElement                            // Предыдущий элемент по ходу воды
    ) {
        super(name);
        this.heaterNumber = heaterNumber;
        this.isSurfaceHeater = true;
        this.selectionNumber = selectionNumber;
        this.hydraulicResistanceInHeater = hydraulicResistanceInHeater;
        this.underheatingOfSteamDrain = underheatingOfSteamDrain;
        this.underheatingOfHeatedMedium = underheatingOfHeatedMedium;

        if (!(heaterNumber == 0)) {
            this.hydraulicResistanceFromSelectionToHeater =         // Считаем потери в труб-де от отбора до подогревателя
                    turbineCylinder.parametersInSelection(selectionNumber).getPressure() * (11 - heaterNumber) / 100;
        }

        this.pressureOfHeatingSteam =                           // Считаем давление в подогревателе
                turbineCylinder.parametersInSelection(selectionNumber).getPressure() - hydraulicResistanceFromSelectionToHeater;
        this.pressureOfSteamDrain = pressureOfHeatingSteam;

        IF97 waterSteam = new IF97(IF97.UnitSystem.DEFAULT);
        this.temperatureOfHeatingSteam =                        // Температура греющего пара
                waterSteam.saturationTemperatureP(pressureOfHeatingSteam) - 273.15;

        this.enthalpyOfHeatingSteam =                           // Энтальпия греющего пара
                turbineCylinder.parametersInSelection(selectionNumber).getEnthalpy();

        if (previousElement.getClass() == Heaters.class) {                          // Если предыдущий элемент - подогреватель
            Heaters previousHeater = (Heaters) previousElement;
            this.pressureOfHeatedMedium =                                           // Давление обогреваемой среды на выходе = давлению обогреваемой среды на выходе из предыдущего элемента - потери в подогревателе
                    previousHeater.getPressureOfHeatedMedium() - hydraulicResistanceInHeater;
            if (Double.isNaN(underheatingOfSteamDrain)) {                           // Если охладитель дренажа отсутствует
                this.temperatureOfSteamDrain = temperatureOfHeatingSteam;               // Температура дренажа = температуре греющего пара
            } else {                                                                // иначе
                this.temperatureOfSteamDrain =                                          // Температура дренажа = температуре обогреваемой среды на выходе из предыдущего подогревателя + недогрев
                        previousHeater.getTemperatureOfHeatedMedium() + underheatingOfSteamDrain;
            }
        } else if (previousElement.getClass() == Pumps.class) {                      // Если предыдущий элемент - насос
            Pumps pump = (Pumps) previousElement;
            this.pressureOfHeatedMedium =                                           // Давление обогреваемой среды на выходе = давлению обогреваемой среды на выходе из насоса - потери в подогревателе
                    pump.getOutletPressure() - hydraulicResistanceInHeater;
            if (Double.isNaN(underheatingOfSteamDrain)) {                           // Если охладитель дренажа отсутствует
                this.temperatureOfSteamDrain = temperatureOfHeatingSteam;               // Температура дренажа = температуре греющего пара
            } else {                                                                // иначе
                this.temperatureOfSteamDrain =                                          // Температура дренажа = температуре обогреваемой среды на выходе из насоса + недогрев
                        pump.getOutletTemperature() + underheatingOfSteamDrain;
            }
        } else {                                                                    // Если предыдущий элемент - Теплосеть
            HeatNetwork heatNetwork = (HeatNetwork) previousElement;
            this.pressureOfHeatedMedium =                                           // Давление обогреваемой среды на выходе = давлению обогреваемой среды на выходе из ТС - потери в подогревателе
                    heatNetwork.getOutletPressure() - hydraulicResistanceInHeater;
            if (Double.isNaN(underheatingOfSteamDrain)) {                           // Если охладитель дренажа отсутствует
                this.temperatureOfSteamDrain = temperatureOfHeatingSteam;               // Температура дренажа = температуре греющего пара
            } else {                                                                // иначе
                this.temperatureOfSteamDrain =                                          // Температура дренажа = температуре обогреваемой среды на выходе из ТС + недогрев
                        heatNetwork.getOutletTemperature() + underheatingOfSteamDrain;
            }
        }


        this.enthalpyOfSteamDrain =                                             // Энтальпия дренажа находится по температуре дренажа на линии насыщения для воды
                waterSteam.specificEnthalpySaturatedLiquidT(temperatureOfSteamDrain + 273.15);

        this.temperatureOfHeatedMedium = temperatureOfHeatingSteam - underheatingOfHeatedMedium;
        this.enthalpyOfHeatedMedium = waterSteam.specificEnthalpyPT(pressureOfHeatedMedium, temperatureOfHeatedMedium + 273.15);
        this.coefficient = 1 - heaterNumber / 1000;
    }

    //-----------------------------Конструктор для смешивающего подогревателя-------------------------------------------
    public Heaters(
            String name,
            double pressureInHeater,
            int selectionNumber,
            TurbineCylinders turbineCylinder,
            Elements previousElement
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

    public void describeHeater() {
        System.out.println("Параметры в " + NAME + " :");
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
        System.out.println("-----------------------------Характеристики дренажа пара----------------------------------------------------------");
        System.out.println("Давление дренажа пара на выходе из подогревателя: " + pressureOfSteamDrain + " ,МПа");
        System.out.println("Температура дренажа пара на выходе из подогревателя: " + temperatureOfSteamDrain + " ,℃");
        System.out.println("Энтальпия дренажа пара на выходе из подогревателя: " + enthalpyOfSteamDrain + " ,кДж/кг");
        System.out.println("-----------------------------Характеристики обогреваемой среды на выходе------------------------------------------");
        System.out.println("Давление обогреваемой среды на выходе из подогревателя: " + pressureOfHeatedMedium + " ,МПа");
        System.out.println("Температура обогреваемой среды на выходе из подогревателя: " + temperatureOfHeatedMedium + " ,℃");
        System.out.println("Энтальпия обогреваемой среды на выходе из подогревателя: " + enthalpyOfHeatedMedium + " ,кДж/кг");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }
}

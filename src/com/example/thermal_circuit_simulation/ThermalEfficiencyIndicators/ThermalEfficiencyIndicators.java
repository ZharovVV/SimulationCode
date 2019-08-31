package com.example.thermal_circuit_simulation.ThermalEfficiencyIndicators;

import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Consumptions;

import java.util.ArrayList;

public class ThermalEfficiencyIndicators {
    private double generatorEfficiency;     // КПД генератора
    private double mechanicalEfficiencyOfTurbogenerator;    // Механический КПД турбогенератора

    /**
     * Расходы пара через отсеки
     */
    private ArrayList<Consumptions> listOfConsumptionThroughTheCompartment;

    /**
     * Теплоперепады отсеков
     */
    private ArrayList<Double> listOfHeatTransferCompartments;

    /**
     * Внутренняя мощность отсеков
     */
    private ArrayList<Double> listOfInternalCompartmentPower;

    /**
     * Гарантированная электрическая мощность, МВт.
     */
    private double guaranteedElectricPower;

    /**
     * Расходы электроэнергии на привод насосов
     */
    private ArrayList<Double> listOfPowerConsumptionForPumpDrive;

    public ThermalEfficiencyIndicators(double generatorEfficiency, double mechanicalEfficiencyOfTurbogenerator) {
        this.generatorEfficiency = generatorEfficiency;
        this.mechanicalEfficiencyOfTurbogenerator = mechanicalEfficiencyOfTurbogenerator;
        listOfConsumptionThroughTheCompartment = new ArrayList<>();
        listOfHeatTransferCompartments = new ArrayList<>();
        listOfPowerConsumptionForPumpDrive = new ArrayList<>();
        listOfInternalCompartmentPower = new ArrayList<>();
    }

    public ArrayList<Consumptions> getListOfConsumptionThroughTheCompartment() {
        return listOfConsumptionThroughTheCompartment;
    }

    public ArrayList<Double> getListOfHeatTransferCompartments() {
        return listOfHeatTransferCompartments;
    }

    public void calculationOfInternalCompartmentPower() {
        if (!listOfInternalCompartmentPower.isEmpty()) {
            return;
        }
        for (Consumptions consumptions : listOfConsumptionThroughTheCompartment) {
            int indexOfCompartment = listOfConsumptionThroughTheCompartment.indexOf(consumptions);
            double heatTransfer = listOfHeatTransferCompartments.get(indexOfCompartment);
            double consumptionValue = consumptions.consumptionValue;
            listOfInternalCompartmentPower.add(indexOfCompartment, heatTransfer * consumptionValue);
        }
    }

    public void calculationOfGuaranteedElectricPower() {
        if (guaranteedElectricPower > 0.0) {
            return;
        }
        for (Double internalCompartmentPower : listOfInternalCompartmentPower) {
            guaranteedElectricPower += internalCompartmentPower;
        }
        guaranteedElectricPower *= 0.98 * generatorEfficiency * mechanicalEfficiencyOfTurbogenerator / 1000;
    }


    public void describe() {
        System.out.println("------------------------------------Показатели тепловой экономичности---------------------------------------------");
        for (Double heatTransferCompartment : listOfHeatTransferCompartments) {
            int index = listOfHeatTransferCompartments.indexOf(heatTransferCompartment);
            double consumptionValue = listOfConsumptionThroughTheCompartment.get(index).consumptionValue;
            double internalCompartmentPower = listOfInternalCompartmentPower.get(index);
            System.out.println("Отсек " + (index + 1) + " Теплоперепад: " + heatTransferCompartment + " ,кДж/кг " +
                    " Расход через отсек: " + consumptionValue + " ,кг/с " +
                    " Теплоперепад: " + internalCompartmentPower + " ,кВт");
        }
        System.out.println();

        System.out.println("Гарантированная электрическая мощность " + guaranteedElectricPower + " ,МВт");

        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }
}
//------------------------------------------------------------------------------------------------------------------
//------------------------------------Показатели тепловой экономичности---------------------------------------------
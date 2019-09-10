package com.example.thermal_circuit_simulation.ThermalEfficiencyIndicators;

import com.example.thermal_circuit_simulation.Elements.Pump;
import com.example.thermal_circuit_simulation.HelperСlassesAndInterfaces.Consumptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
     * Расходы электроэнергии на привод насосов, МВт.
     */
    private HashMap<Pump, Double> mapOfPowerConsumptionForPumpDrive;

    /**
     * Расход электроэнергии на собственные нужды, МВт.
     */
    private double electricityConsumptionForOwnNeeds;

    /**
     * Расход теплоты на турбоустановку для производства элекроэнергии, МВт.
     */
    private double heatConsumptionForATurbineForElectricityGeneration;

    private double steamConsumptionToTheTurbine; // Расход пара на турбину
    private double inletEnthalpy;           // Энтальпия на входе в ПГ (на выходе из последнего подогревателя)
    private double outletEnthalpy;          // Энтальпия на выходе из ПГ (на входе в цилиндр)
    private double thermalPowerOfHeatNetwork;    // Тепловая мощность тепловой сети

    /**
     * Удельный расход теплоты брутто на производство электроэнергии, кВт/кВт.
     */
    private double specificGrossHeatConsumptionForElectricityProduction;

    private double turboPower;                // Мощность ТП

    /**
     * Электрический КПД брутто
     */
    private double electricalGrossEfficiency;

    /**
     * Электрический КПД нетто
     */
    private double netElectricalEfficiency;


    public ThermalEfficiencyIndicators(double generatorEfficiency, double mechanicalEfficiencyOfTurbogenerator) {
        this.generatorEfficiency = generatorEfficiency;
        this.mechanicalEfficiencyOfTurbogenerator = mechanicalEfficiencyOfTurbogenerator;
        listOfConsumptionThroughTheCompartment = new ArrayList<>();
        listOfHeatTransferCompartments = new ArrayList<>();
        mapOfPowerConsumptionForPumpDrive = new HashMap<>();
        listOfInternalCompartmentPower = new ArrayList<>();
    }

    public void setSteamConsumptionToTheTurbine(double steamConsumptionToTheTurbine) {
        this.steamConsumptionToTheTurbine = steamConsumptionToTheTurbine;
    }

    public void setInletEnthalpy(double inletEnthalpy) {
        this.inletEnthalpy = inletEnthalpy;
    }

    public void setOutletEnthalpy(double outletEnthalpy) {
        this.outletEnthalpy = outletEnthalpy;
    }

    public void setThermalPowerOfHeatNetwork(double thermalPowerOfHeatNetwork) {
        this.thermalPowerOfHeatNetwork = thermalPowerOfHeatNetwork;
    }

    public void setTurboPower(double turboPower) {
        this.turboPower = turboPower;
    }

    public ArrayList<Consumptions> getListOfConsumptionThroughTheCompartment() {
        return listOfConsumptionThroughTheCompartment;
    }

    public ArrayList<Double> getListOfHeatTransferCompartments() {
        return listOfHeatTransferCompartments;
    }

    public HashMap<Pump, Double> getMapOfPowerConsumptionForPumpDrive() {
        return mapOfPowerConsumptionForPumpDrive;
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

    public void calculationOfElectricityConsumptionForOwnNeeds() {
        if (electricityConsumptionForOwnNeeds > 0.0) {
            return;
        }
        for (Double value : mapOfPowerConsumptionForPumpDrive.values()) {
            electricityConsumptionForOwnNeeds += value;
        }
    }

    public void calculationOfHeatConsumptionForATurbineForElectricityGeneration() {
        heatConsumptionForATurbineForElectricityGeneration = steamConsumptionToTheTurbine * (outletEnthalpy - inletEnthalpy) / 1000 - thermalPowerOfHeatNetwork;
    }

    public void calculationOfSpecificGrossHeatConsumptionForElectricityProduction() {
        specificGrossHeatConsumptionForElectricityProduction = heatConsumptionForATurbineForElectricityGeneration / (guaranteedElectricPower + turboPower);
    }

    public void calculationOfElectricalEfficiency() {
        electricalGrossEfficiency = (guaranteedElectricPower + turboPower) / heatConsumptionForATurbineForElectricityGeneration * 100;
        netElectricalEfficiency = (guaranteedElectricPower - electricityConsumptionForOwnNeeds) / heatConsumptionForATurbineForElectricityGeneration * 100;
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

        System.out.println();
        System.out.println("Расходы электроэнергии на приводы насосов: ");
        for (Map.Entry<Pump, Double> pumpsDoubleEntry : mapOfPowerConsumptionForPumpDrive.entrySet()) {
            Pump pump = pumpsDoubleEntry.getKey();
            double power = mapOfPowerConsumptionForPumpDrive.get(pump);
            System.out.println(pump.NAME + " : " + power + " ,МВт");
        }
        System.out.println();
        System.out.println("Расход электроэнергии на собственные нужды: " + electricityConsumptionForOwnNeeds + ", МВт");
        System.out.println();
        System.out.println("Расход теплоты на турбоустановку для производства элекроэнергии: " + heatConsumptionForATurbineForElectricityGeneration + ", МВт");
        System.out.println();
        System.out.println("Удельный расход теплоты брутто на производство электроэнергии: " + specificGrossHeatConsumptionForElectricityProduction + ", кВт/кВт");
        System.out.println();
        System.out.println("Электрический КПД брутто: " + electricalGrossEfficiency + " %");
        System.out.println("Электрический КПД нетто: " + netElectricalEfficiency + " %");
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }
}
//------------------------------------------------------------------------------------------------------------------
//------------------------------------Показатели тепловой экономичности---------------------------------------------
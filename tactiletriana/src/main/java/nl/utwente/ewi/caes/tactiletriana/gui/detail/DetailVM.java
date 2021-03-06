/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail;

import nl.utwente.ewi.caes.tactiletriana.gui.detail.chart.ChartVM;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.datetime.DateTimeVM;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.weather.WeatherVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.LoggingEntityVMBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.LoggingEntityBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author Richard
 */
public class DetailVM {

    private final Simulation simulation;
    private final DateTimeVM dateTimeVM;
    private final WeatherVM weatherVM;
    private final ChartVM chartVM;
    private final ChartVM[] subChartVMs;
    private final LoggingEntityVMBase[] loggersOnCharts;
    int subChartIndex = 0;

    public DetailVM(Simulation simulation) {
        this.simulation = simulation;

        dateTimeVM = new DateTimeVM(simulation);
        chartVM = new ChartVM();
        weatherVM = new WeatherVM(simulation);
        subChartVMs = new ChartVM[3];
        for (int i = 0; i < 3; i++) {
            subChartVMs[i] = new ChartVM();
        }
        loggersOnCharts = new LoggingEntityVMBase[3];
    }

    // METHODS

    /**
     * Shows a LoggingEntityBase on a chart.
     *
     * @param loggerVM the VM for the LoggingEntityBase
     * @param actual the actual LoggingEntityBase to be shown on a chart
     * @param future the future LoggingEntityBase to be shown on a chart
     */
    public void showOnChart(LoggingEntityVMBase loggerVM, LoggingEntityBase actual, LoggingEntityBase future) {
        loggerVM.setShownOnChart(true);
        loggerVM.setChartIndex(subChartIndex);
        
        for (int i = 0; i < loggersOnCharts.length; i++) {
            if (loggersOnCharts[(i + subChartIndex) % loggersOnCharts.length] == null) {
                subChartIndex = subChartIndex + i;
                break;
            }
        }

        LoggingEntityVMBase old = loggersOnCharts[subChartIndex];
        loggersOnCharts[subChartIndex] = loggerVM;
        subChartVMs[subChartIndex].setEntity(actual, future);
        subChartIndex++;
        subChartIndex = (subChartIndex == subChartVMs.length) ? 0 : subChartIndex;

        if (old != null) {
            old.setShownOnChart(false);
        }
    }

    /**
     * Removes a LoggingEntityBase from the charts.
     *
     * @param loggerVM the VM for the LoggingEntityBase
     */
    public void removeFromChart(LoggingEntityVMBase loggerVM) {
        loggerVM.setShownOnChart(false);
        loggerVM.setChartIndex(-1);

        for (int i = 0; i < subChartVMs.length; i++) {
            if (loggersOnCharts[i] == loggerVM) {
                subChartVMs[i].setEntity(null, null);
            }
        }
    }
    
    /**
     * Resets the view to its initial state
     */
    public void reset() {
        for (int i = 0; i < 3; i++) {
            if (subChartVMs[i] != null) {
                subChartVMs[i] = null;
            }
        }
        subChartIndex = 0;
    }

    // CALLBACKS FOR VIEW
    /**
     * To be called by the view when it gets coupled to this VM. Describes a
     * function that should be called when the Simulation jumps to a new
     * TimeSpan.
     *
     * @param callback the function that should be called
     */
    public void setOnSimulationTimeSpanChange(Runnable callback) {
        this.simulation.addOnTimeSpanShiftedHandler(callback);
    }

    // CHILD VMs
    public DateTimeVM getDateTimeVM() {
        return dateTimeVM;
    }

    public ChartVM getChartVM() {
        return chartVM;
    }

    public WeatherVM getWeatherVM() {
        return weatherVM;
    }

    public ChartVM getSubChartVM(int index) {
        return subChartVMs[index];
    }
}

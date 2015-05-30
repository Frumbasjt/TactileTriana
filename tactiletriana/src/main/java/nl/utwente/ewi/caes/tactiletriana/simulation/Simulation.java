/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import nl.utwente.ewi.caes.tactiletriana.Concurrent;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.UncontrollableLoad;

/**
 *
 * @author Richard
 */
public class Simulation extends SimulationBase {
    private static final TimeScenario DEFAULT_SCENARIO = new TimeScenario(new TimeScenario.TimeSpan(LocalDate.of(2014, 1, 1), LocalDate.of(2014, 12, 31)));
    
    public Simulation() {
        this.setState(SimulationState.STOPPED);

        // Initialise time
        setCurrentTime(getTimeScenario().getCurrentTime());
    }

    // PROPERTIES
    
    /**
     * The state of the simulation.
     */
    private final ReadOnlyObjectWrapper<SimulationState> state = new ReadOnlyObjectWrapper<>();
    
    public ReadOnlyObjectProperty<SimulationState> stateProperty() {
        return state.getReadOnlyProperty();
    }

    private void setState(SimulationState state){
        this.state.set(state);
    }
    
    public SimulationState getState(){
        return this.stateProperty().get();
    }
    
    /**
     * The time scenario that this simulation follows.
     */
    private final ObjectProperty<TimeScenario> timeScenario = new SimpleObjectProperty<>(DEFAULT_SCENARIO);
    
    public ObjectProperty<TimeScenario> timeScenarioProperty() {
        return timeScenario;
    }
    
    public final TimeScenario getTimeScenario() {
        return timeScenarioProperty().get();
    }
    
    public final void setTimeScenario(TimeScenario timeScenario) {
        timeScenarioProperty().set(timeScenario);
    }
    
    // EVENT HANDLING
    
    private final List<Runnable> timeSpanShiftedCallbacks = new ArrayList<>();
    
    public final void addOnTimeSpanShiftedHandler(Runnable handler) {
        timeSpanShiftedCallbacks.add(handler);
    }
    
    public final void removeOnTimeSpanShiftedHandler(Runnable handler) {
        timeSpanShiftedCallbacks.remove(handler);
    }
    
    
    // PUBLIC METHODS
    
    private boolean startedOnce = false;
    
    /**
     * Starts the simulation if it is stopped, or resumes it if it is paused.
     * If the simulation is stopped, it will start a new thread to run the simulation
     * on.
     */
    public void start() {
        if (!startedOnce){      
            startedOnce = true;
            Concurrent.getExecutorService().scheduleAtFixedRate(() -> {
                if (this.getState() == SimulationState.RUNNING){
                    tick();
                }
            }, SimulationConfig.SYSTEM_TICK_TIME, SimulationConfig.SYSTEM_TICK_TIME, TimeUnit.MILLISECONDS);
        }
        setState(SimulationState.RUNNING);
    }
    

    /**
     * Pauses the simulation if it is running. Unlike stop, this method does not
     * shut down the simulation thread.
     */
    public void pause() {
        setState(SimulationState.PAUSED);
    }

    /**
     * Resets all values to default.
     */
    public void reset() {
        setState(SimulationState.STOPPED);
        int i = 0;
        for (House house : houses) {
            house.repairFuse();
            house.getDevices().clear();
            house.getDevices().add(new UncontrollableLoad(i, this));
            i++;
        }
        for (Cable cable : internalCables) {
            cable.repair();
        }
        for (Cable cable : houseCables) {
            cable.repair();
        }
        clearAllLogs();
        getTimeScenario().reset();
        setCurrentTime(getTimeScenario().getCurrentTime());
    }
    
    /**
     * Increments the time by either the tick time, or leaps to the next time span
     * if required.
     */
    @Override
    protected void incrementTime() {
        boolean timeSpanShifted = getTimeScenario().next(SimulationConfig.TICK_MINUTES);
        setCurrentTime(getTimeScenario().getCurrentTime());
        if (timeSpanShifted) {
            clearAllLogs();
            for (Runnable callback : timeSpanShiftedCallbacks) {
                callback.run();
            }
        }
    }

    // HELP METHODS
    
    private void clearAllLogs() {
        for (House house : houses) {
            house.getLog().clear();
        }
        for (Node node : internalNodes) {
            node.getLog().clear();
        }
        for (Node node : houseNodes) {
            node.getLog().clear();
        }
        for (Cable cable : internalCables) {
            cable.getLog().clear();
        }
        for (Cable cable : houseCables) {
            cable.getLog().clear();
        }
        transformer.getLog().clear();
        for (Cable cable : transformer.getCables()) {
            cable.getLog().clear();
        }
        this.getLog().clear();
    }
    
    // NESTED ENUMS
    
    /**
     * Describes the state the Simulation is in.
     */
    public static enum SimulationState {
        /**
         * The Simulation is running.
         */
        RUNNING, 
        /**
         * The Simulation is paused.
         */
        PAUSED,
        /**
         * The Simulation is in its initial state.
         */
        STOPPED 
    };
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.configuration;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;
import nl.utwente.ewi.caes.tactiletriana.gui.configuration.scenario.ScenarioVM;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author Richard
 */
public class ConfigurationVM {

    private static final SimpleBooleanProperty launched = new SimpleBooleanProperty(false);
    private final ObservableList screenIndexList = FXCollections.observableArrayList();
    private final ScenarioVM scenarioVM = new ScenarioVM();
    
    private Simulation simulation;
    
    public ConfigurationVM(Simulation simulation) {
        this.simulation = simulation;

        // full screen checkbox is enabled when application hasn't launched yet
        fullScreenCheckBoxDisabled.bind(launched);

        // combo boxes are enabled when full screen is checked, and the application hasn't launched yet
        screenComboBoxesDisabled.bind(fullScreenChecked.not().or(launched));

        // start button is enabled when a valid port is chosen and combo boxes are not
        // at default position or fullscreen is not checked
        startButtonDisabled.bind(Bindings.createBooleanBinding(() -> {
            try {
                int port = Integer.parseInt(portFieldText.get());
                if (port < 1024 || port > 65535) {
                    return true;
                }
            } catch (NumberFormatException e) {
                return true;
            }

            if (!fullScreenChecked.get()) {
                return false;
            } else {
                return (touchScreenSelection.get() == null || detailScreenSelection.get() == null);
            }
        }, fullScreenChecked, touchScreenSelection, detailScreenSelection, portFieldText));

        // start button text is set to "Start" when the simulation hasn't started yet,
        // and "Resume" when it already has
        startButtonText.bind(Bindings.createStringBinding(() -> {
            if (simulation.getState() == Simulation.SimulationState.PAUSED) {
                return "Resume Simulation";
            } else {
                return "Start Simulation";
            }
        }, this.simulation.stateProperty()));

        // reset button is enabled when simulation has been started
        resetButtonDisabled.bind(Bindings.createBooleanBinding(() -> { 
            return simulation.getState() == Simulation.SimulationState.STOPPED;
        }, this.simulation.stateProperty()));
        
        // scenario is enabled when simulation is stopped
        scenarioViewDisable.bind(Bindings.createBooleanBinding(() -> { 
            return simulation.getState() != Simulation.SimulationState.STOPPED;
        }, this.simulation.stateProperty()));
    }

    // BINDABLE PROPERTIES
    
    /**
     * Whether the Scenario view is enabled
     */
    private final BooleanProperty scenarioViewDisable = new SimpleBooleanProperty(false);
    
    public ReadOnlyBooleanProperty scenarioViewDisableProperty() {
        return scenarioViewDisable;
    }
    
    public final boolean isScenarioViewDisable() {
        return scenarioViewDisableProperty().get();
    }
    
    /**
     * The text entered in the port field
     */
    private final StringProperty portFieldText = new SimpleStringProperty("4321");

    public StringProperty portFieldTextProperty() {
        return this.portFieldText;
    }

    /**
     * Whether the full screen check box is checked
     */
    private final BooleanProperty fullScreenChecked = new SimpleBooleanProperty(false);

    public BooleanProperty fullScreenCheckedProperty() {
        return this.fullScreenChecked;
    }

    /**
     * Whether the full screen check box is enabled
     */
    private final BooleanProperty fullScreenCheckBoxDisabled = new SimpleBooleanProperty(false);

    public BooleanProperty fullScreenCheckBoxDisabledProperty() {
        return fullScreenCheckBoxDisabled;
    }

    /**
     * Whether the combo boxes are enabled
     */
    private final BooleanProperty screenComboBoxesDisabled = new SimpleBooleanProperty(true);

    public BooleanProperty screenComboBoxesDisabledProperty() {
        return this.screenComboBoxesDisabled;
    }

    /**
     * Whether the start button is disabled
     */
    private final BooleanProperty startButtonDisabled = new SimpleBooleanProperty(false);

    public BooleanProperty startButtonDisabledProperty() {
        return this.startButtonDisabled;
    }

    /**
     * Whether the reset button is disabled
     */
    private final BooleanProperty resetButtonDisabled = new SimpleBooleanProperty(false);

    public BooleanProperty resetButtonDisabledProperty() {
        return this.resetButtonDisabled;
    }

    /**
     * The text to be shown on the start button
     */
    private final StringProperty startButtonText = new SimpleStringProperty("Start Simulation");

    public StringProperty startButtonTextProperty() {
        return this.startButtonText;
    }

    /**
     * The screen number that is chosen for the touch screen, null if none is
     * chosen yet
     */
    private final ObjectProperty touchScreenSelection = new SimpleObjectProperty(null);

    public ObjectProperty touchScreenSelectionProperty() {
        return this.touchScreenSelection;
    }

    /**
     * The screen number that is chosen for the detail screen, null if none is
     * chosen yet
     */
    private final ObjectProperty detailScreenSelection = new SimpleObjectProperty(null);

    public ObjectProperty detailScreenSelectionProperty() {
        return this.detailScreenSelection;
    }

    /**
     *
     * @return an ObservableList of screen indexes
     */
    public ObservableList<Integer> getScreenIndexList() {
        return screenIndexList;
    }
    
    /**
     * @return the VM for the ScenarioView
     */
    public ScenarioVM getScenarioVM() {
        return scenarioVM;
    }

    // METHODS

    public void start() {
        
        
        StageController.getInstance().setMainStagesVisible(true);
        StageController.getInstance().setLauncherStageVisible(false);
        StageController.getInstance().setScreenIndexStagesVisible(false);
        
        if (this.simulation.getState() == Simulation.SimulationState.STOPPED) {
            
            this.simulation.setTimeScenario(scenarioVM.build());
        }
        
        
        // save the configured timescenario
        SimulationConfig.SaveProperty("timescenario", this.simulation.getTimeScenario().toString());
        
        // save the configuration
        SimulationConfig.SaveProperties();       
        
        this.simulation.start();

        launched.set(true);
    }

    public void reset() {
        StageController.getInstance().resetSimulation();
    }
}

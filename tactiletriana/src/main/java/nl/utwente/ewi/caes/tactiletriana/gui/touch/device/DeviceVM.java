/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.deviceconfig.DeviceConfigVM;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.LoggingEntityVMBase;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseVM;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.BufferBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.TrianaHouseController;

/**
 *
 * @author Richard
 */
public class DeviceVM extends LoggingEntityVMBase {

    private DeviceBase model;
    private HouseVM house;
    private DeviceConfigVM deviceConfigVM;

    public DeviceVM(DeviceBase model) {
        this.model = model;

        deviceConfigVM = new DeviceConfigVM(model);

        // Bind current consumption divided by 3700 (assumed maximum consumption) to load
        load.bind(Bindings.createDoubleBinding(() -> {
            return Math.min(1.0, Math.abs(model.getCurrentConsumption()) / 3700d);
        }, model.currentConsumptionProperty()));

        // Bind state of charge
        stateOfCharge.set(-1d);
        if (model instanceof BufferBase) {
            BufferBase b = (BufferBase) model;
            stateOfCharge.bind(b.stateOfChargeProperty().divide(b.capacityProperty()));
        }

        // Define states for view
        state.bind(Bindings.createObjectBinding(() -> {
            if (model.getState() != DeviceBase.State.CONNECTED) {
                return State.DISCONNECTED;
            }
            if (model.getCurrentConsumption() < 0) {
                return State.PRODUCING;
            } else {
                return State.CONSUMING;
            }
        }, model.currentConsumptionProperty(), model.stateProperty()));

        // Show config icon if there are parameters to configure, and the device is not on the stack
        configIconShown.bind(Bindings.createBooleanBinding(() -> {
            return deviceConfigVM.getRows().size() > 0 && !isOnStack();
        }, onStack));
        
        // Show battery icon if device is not on the stack, and is a BufferBase
        batteryIconVisible.bind(Bindings.createBooleanBinding(() -> {
            return model instanceof BufferBase && !isOnStack();
        }, onStack));
    }

    /**
     *
     * @return the view model for the configuration view for this device
     */
    public DeviceConfigVM getDeviceConfigVM() {
        return deviceConfigVM;
    }

    /**
     *
     * @return the class of the device
     */
    public Class<? extends DeviceBase> getModelClass() {
        return this.model.getClass();
    }

    /**
     *
     * @return The text for the header the device view
     */
    public final String getHeader() {
        return model.getDisplayName();
    }

    /**
     * The load of the device on a scale from 0 to 1. Computed as device
     * consumption divided by 3700, which is assumed to be the maximum any
     * single device will ever consume/produce.
     */
    private final ReadOnlyDoubleWrapper load = new ReadOnlyDoubleWrapper();

    public ReadOnlyDoubleProperty loadProperty() {
        return load.getReadOnlyProperty();
    }

    public final double getLoad() {
        return loadProperty().get();
    }

    /**
     * The state of charge of the device, a value of 0 to 1. A negative value if
     * the device does not have a battery.
     */
    private final ReadOnlyDoubleWrapper stateOfCharge = new ReadOnlyDoubleWrapper();

    public ReadOnlyDoubleProperty stateOfChargeProperty() {
        return stateOfCharge.getReadOnlyProperty();
    }

    public double getStateOfCharge() {
        return stateOfCharge.get();
    }

    /**
     * The current visual state of the view
     */
    private final ReadOnlyObjectWrapper<State> state = new ReadOnlyObjectWrapper<>();

    public ReadOnlyObjectProperty<State> stateProperty() {
        return state.getReadOnlyProperty();
    }

    public final State getState() {
        return state.get();
    }

    /**
     * Whether the configuration icon should be shown
     */
    private final ReadOnlyBooleanWrapper configIconShown = new ReadOnlyBooleanWrapper(false);

    public ReadOnlyBooleanProperty configIconShownProperty() {
        return configIconShown.getReadOnlyProperty();
    }

    public final boolean isConfigIconShown() {
        return configIconShown.get();
    }

    /**
     * Whether the configuration panel should be shown
     */
    private final ReadOnlyBooleanWrapper configPanelShown = new ReadOnlyBooleanWrapper(false);

    public ReadOnlyBooleanProperty configPanelShownProperty() {
        return configPanelShown.getReadOnlyProperty();
    }

    public boolean isConfigPanelShown() {
        return configPanelShown.get();
    }

    private void setConfigPanelShown(boolean configPanelShown) {
        this.configPanelShown.set(configPanelShown);
    }

    /**
     * Whether the configuration panel should be shown
     */
    private final ReadOnlyBooleanWrapper batteryIconVisible = new ReadOnlyBooleanWrapper(false);

    public ReadOnlyBooleanProperty batteryIconVisibleProperty() {
        return batteryIconVisible.getReadOnlyProperty();
    }

    public final boolean isBatteryIconVisible() {
        return batteryIconVisibleProperty().get();
    }
    
    /**
     * Whether the device is on the stack of devices. A device is initialised as
     * being on the stack, but when it has left it it can never return to the
     * stack.
     */
    private final BooleanProperty onStack = new SimpleBooleanProperty(true);

    public ReadOnlyBooleanProperty onStackProperty() {
        return onStack;
    }
    
    public final boolean isOnStack() {
        return onStackProperty().get();
    }
    
    public final void removeFromStack() {
        onStack.set(false);
    }
    
    // EVENT HANDLING
    /**
     * Called when the device view is dropped on a house or left it
     *
     * @param house the HouseVM associated with the HouseView that the device
     * was dropped on, null if it left a house
     */
    public void droppedOnHouse(HouseVM house) {
        if (this.house == house) {
            return;
        }

        if (this.house != null) {
            this.house.getModel().getDevices().remove(model);
        }

        this.house = house;

        if (house != null) {
            house.getModel().getDevices().add(model);
        }
    }

    /**
     * Opens or closes the configuration panel for this device, if there is one
     */
    public void configIconPressed() {
        setConfigPanelShown(!isConfigPanelShown());
    }

    /**
     * Shows the device on the chart
     */
    public void longPressed() {
        if (model.getState() != DeviceBase.State.NOT_IN_HOUSE && !(model instanceof TrianaHouseController)) {
            StageController.getInstance().showOnChart(this, model);
        }
    }

    // NESTED ENUMS
    /**
     * The different states of the model that the view should reflect
     */
    public enum State {

        /**
         * The device is not connected
         */
        DISCONNECTED,
        /**
         * The device is producing power
         */
        PRODUCING,
        /**
         * The device is consuming power
         */
        CONSUMING
    }
}

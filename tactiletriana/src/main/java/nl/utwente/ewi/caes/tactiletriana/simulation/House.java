/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author Richard
 */
public class House extends LoggingEntityBase {

    private final ObservableList<DeviceBase> devices;

    public House(Simulation simulation) {
        super(LoggedValueType.POWER, "House", simulation);

        devices = FXCollections.observableArrayList();
        devices.addListener((ListChangeListener.Change<? extends DeviceBase> c) -> {
            while (c.next()) {
                for (DeviceBase removedDevice : c.getRemoved()) {
                    removedDevice.setState(DeviceBase.State.NOT_IN_HOUSE);
                }
            }
        });
    }

    /**
     * @return The devices that are connected to the network.
     */
    public ObservableList<DeviceBase> getDevices() {
        return devices;
    }

    /*
     * The amount of power the house currently consumes. A negative number means
     * the house is producing energy.
     */
    private final ReadOnlyDoubleWrapper currentConsumption = new ReadOnlyDoubleWrapper(0.0) {
        @Override
        public void set(double value) {
            if (value > getMaximumConsumption()) {
                value = 0;
                setFuseBlown(true);
            }
            log(value);
            super.set(value);
        }
    };

    public ReadOnlyDoubleProperty currentConsumptionProperty() {
        return currentConsumption.getReadOnlyProperty();
    }

    public final double getCurrentConsumption() {
        return currentConsumptionProperty().get();
    }

    protected final void setCurrentConsumption(double value) {
        this.currentConsumption.set(value);
    }

    /**
     * The absolute maximum of power the house can consume/produce. When more
     * than this is consumed, the fuse in the house will blow.
     */
    
    /*
    * Deze waarde is het aantal ampere dat een huis maximaal mag hebben kan hebben.
    */
    public static final int HOUSE_MAX_FUSE_CURRENT = 3 * 35;
    private final ReadOnlyDoubleWrapper maximumConsumption = new ReadOnlyDoubleWrapper(230 * HOUSE_MAX_FUSE_CURRENT) {
        @Override
        public void set(double value) {
            setAbsoluteMaximum(value);
            super.set(value);
        }
    };

    public ReadOnlyDoubleProperty maximumConsumptionProperty() {
        return maximumConsumption;
    }

    public final double getMaximumConsumption() {
        return maximumConsumptionProperty().get();
    }

    /**
     * Whether the fuse is blown or not.
     */
    private final ReadOnlyBooleanWrapper fuseBlown = new ReadOnlyBooleanWrapper(false);

    public ReadOnlyBooleanProperty fuseBlownProperty() {
        return fuseBlown.getReadOnlyProperty();
    }

    public final boolean isFuseBlown() {
        return fuseBlownProperty().get();
    }

    private void setFuseBlown(boolean fuseBlown) {
        this.fuseBlown.set(fuseBlown);
    }

    /**
     * Repairs the fuse. If more power than the maximum is still
     * produced/consumed, the fuse will blow again immediately.
     */
    public void repairFuse() {
        fuseBlown.set(false);
    }

    public void tick(Simulation simulation, boolean connected) {
        if (isFuseBlown()) {
            connected = false;
        }

        for (DeviceBase device : getDevices()) {
            device.tick(simulation, connected);
        }

        setCurrentConsumption(getDevices().stream().mapToDouble(d -> d.getCurrentConsumption()).sum());

        log(getCurrentConsumption());
    }

    public String toString(int indentation) {
        String output = "";
        for (int i = 0; i < indentation; i++) {
            output += "\t";
        }
        output += "|-";

        output += "(House:P=" + getCurrentConsumption() + ")";

        return output;
    }
}

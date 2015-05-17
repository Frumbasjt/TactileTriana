/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Richard
 */
public abstract class DeviceBase extends LoggingEntityBase {
    private static int DEVICE_ID = 0;
    private final int id;
    private final String apiDeviceType;
    private final Set<String> apiProperties;
    private final List<Property> properties;
    
    /**
     * Constructs a new DeviceBase
     * 
     * @param simulation    the simulation that this device is part of
     * @param displayName   the name of the device as it should be shown to the user
     * @param apiDeviceType    the name of the device as specified in the API
     */
    public DeviceBase(SimulationBase simulation, String displayName, String apiDeviceType) {
        super(simulation, displayName, QuantityType.POWER);
        
        id = DEVICE_ID;
        DEVICE_ID++;
        
        this.apiDeviceType = apiDeviceType;
        this.apiProperties = new HashSet<>();
        this.properties = new ArrayList<>();
    }

    // PROPERTIES
    
    /**
     * @return a unique identifier for this device
     */
    public int getId() {
        return this.id;
    }
    
    /**
     * The amount of power that the device currently consumes
     */
    private final ReadOnlyDoubleWrapper currentConsumption = new ReadOnlyDoubleWrapper(10.0) {
        @Override
        public void set(double value) {
            // consumption is always zero if not connected to the grid
            if (getState() != DeviceBase.State.CONNECTED) {
                value = 0;
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
        currentConsumption.set(value);
    }
    
    /**
     * The house that hosts this device
     */
    private final ReadOnlyObjectWrapper<House> parentHouse = new ReadOnlyObjectWrapper<>();
    
    public ReadOnlyObjectProperty<House> parentHouseProperty() {
        return parentHouse.getReadOnlyProperty();
    }
    
    public House getParentHouse() {
        return parentHouse.get();
    }
    
    void setParentHouse(House house) {
        parentHouse.set(house);
    }

    /**
     *
     * @return the state of this device
     */
    private final ObjectProperty<State> state = new SimpleObjectProperty<State>(DeviceBase.State.NOT_IN_HOUSE) {
        @Override
        public void set(State value) {
            if (value != DeviceBase.State.CONNECTED) {
                // when not connected, no consumption
                setCurrentConsumption(0);
            }
            super.set(value);
        }
    };

    public ObjectProperty<State> stateProperty() {
        return this.state;
    }

    public final State getState() {
        return stateProperty().get();
    }

    protected final void setState(State s) {
        this.stateProperty().set(s);
    }
    
    /**
     * Returns the set of property keys that the device has, as specified in the 
     * API documentation. These are they key values for the JSON representation
     * of this device.
     * 
     * @return the set of property keys
     */
    public final Set<String> getAPIProperties() {
        return Collections.unmodifiableSet(apiProperties);
    }
    
    /**
     * Returns the set of properties of this device that may change over the 
     * course over the Simulation. Used by SimulationPrediction to track changes
     * in a device.
     * 
     * @return the set of properties
     */
    public final List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }
    
    // METHODS

    public void tick(boolean connected) {
        if (!connected) {
            setState(DeviceBase.State.DISCONNECTED);
        } else {
            setState(DeviceBase.State.CONNECTED);
        }
    }
    
    protected final void registerAPIProperty(String apiProperty) {
        this.apiProperties.add(apiProperty);
    }
    
    protected final void registerProperty(Property property) {
        this.properties.add(property);
    }
    
    // API
    
    /**
     * Convert this Device and its parameters to a JSON representation as specified 
     * in the API.
     * 
     * @return the JSON representation of the device
     */
    public final JSONObject toJSON() {
        JSONObject result = new JSONObject();
        result.put("deviceID", this.id);
        result.put("deviceType", this.apiDeviceType);
        result.put("consumption", this.getCurrentConsumption());
        
        result.put("parameters", parametersToJSON());
        
        return result;
    }
    
    /**
     * Creates a JSON representation of the parameters of this device as specified
     * in the API.
     * 
     * @return the JSON representation of the device's parameters
     */
    protected abstract JSONObject parametersToJSON();
    
    /**
     * Sets a property to a certain value.
     * 
     * @param property  the key of the property to be set (the JSON property name)
     * @param value     the value that the property should be set to
     * @throws IllegalArgumentException if the device does not know the given property, or cannot apply the given value to it.
     */
    public void setProperty(String property, boolean value) {
        throw new IllegalArgumentException("Cannot set property " + property);
    }
    
    /**
     * Sets a property to a certain value.
     * 
     * @param property  the key of the property to be set (the JSON property name)
     * @param value     the value that the property should be set to
     * @throws IllegalArgumentException if the device does not know the given property, or cannot apply the given value to it.
     */
    public void setProperty(String property, double value) {
        throw new IllegalArgumentException("Cannot set property " + property);
    }
    
    /**
     * Sets a property to a certain value.
     * 
     * @param property  the key of the property to be set (the JSON property name)
     * @param value     the value that the property should be set to
     * @throws IllegalArgumentException if the device does not know the given property, or cannot apply the given value to it.
     */
    public void setProperty(String property, int value) {
        throw new IllegalArgumentException("Cannot set property " + property);
    }
    
    /**
     * Sets a property to a certain value.
     * 
     * @param property  the key of the property to be set (the JSON property name)
     * @param value     the value that the property should be set to
     * @throws IllegalArgumentException if the device does not know the given property, or cannot apply the given value to it.
     */
    public void setProperty(String property, JSONArray value) {
        throw new IllegalArgumentException("Cannot set property " + property);
    }
    
    /**
     * Sets a property to a certain value.
     * 
     * @param property  the key of the property to be set (the JSON property name)
     * @param value     the value that the property should be set to
     * @throws IllegalArgumentException if the device does not know the given property, or cannot apply the given value to it.
     */
    public void setProperty(String property, JSONObject value) {
        throw new IllegalArgumentException("Cannot set property " + property);
    }
    
    // ENUMS AND NESTED CLASSES
    
    /**
     * Describes the state of a device
     */
    public enum State {

        /**
         * The device is not connected to a house
         */
        NOT_IN_HOUSE,
        /**
         * The device is connected to a house
         */
        CONNECTED,
        /**
         * The device is connected to a house, but can't draw power
         */
        DISCONNECTED,
    }
}

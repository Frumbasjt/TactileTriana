/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import com.sun.javafx.collections.ObservableListWrapper;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Richard
 */
public class House extends HouseBase {
    
    private ObservableList<DeviceBase> devices;
    
    public House(){
        devices = FXCollections.observableArrayList();
        
        currentConsumption.bind(Bindings.createDoubleBinding(() -> { 
            double sum = 0.0;
            for (DeviceBase device : devices) {
                sum += device.getCurrentConsumption();
            }
            return sum;
        }, devices));
    }
    
    @Override
    public ObservableList<DeviceBase> getDevices() {
        return devices;
    }
    
    @Override
    public String toString(){
        return "(House:P="+getCurrentConsumption()+")";
    }
    
    private final ReadOnlyDoubleWrapper currentConsumption = new ReadOnlyDoubleWrapper(0.0);
    
    @Override
    public ReadOnlyDoubleProperty currentConsumptionProperty() {
        return currentConsumption.getReadOnlyProperty();
    }
}
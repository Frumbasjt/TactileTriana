/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.ArrayList;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

/**
 * A connection between to nodes
 */
public class Cable implements ISimulationEntity {
    private final Node childNode;
    private final double resistance;
    private final double length;    
    
    /**
     * Instantiates a new cable connected to two nodes
     * @param childNode The node away from the transformer
     */
    public Cable(Node childNode) {
        this.childNode = childNode;
        
        this.resistance = 0.00005;
        this.length = 10;
    }
    
    /**
     * The current that flows through the current measured in ampere.
     */
    private final ReadOnlyDoubleWrapper current = new ReadOnlyDoubleWrapper(0.0) {
        @Override
        public void set(double value) {
            if (Math.abs(value) > getMaximumCurrent()) {
                setBroken(true);
            }
            super.set(value);
        }
    };

    public ReadOnlyDoubleProperty currentProperty() {
        return current.getReadOnlyProperty();
    }
    
    public final double getCurrent() {
        return currentProperty().get();
    }
    
    private void setCurrent(double value) {
        current.set(value);
    }
    
    /**
     * The absolute maximum current that can flow through the cable before it breaks;
     */
    private ReadOnlyDoubleWrapper maximumCurrent = new ReadOnlyDoubleWrapper(100d);
    
    public ReadOnlyDoubleProperty maximumCurrentProperty() {
        return maximumCurrent.getReadOnlyProperty();
    }
    
    public final double getMaximumCurrent() {
        return maximumCurrentProperty().get();
    }
    
    private void setMaximumCurrent(double maximumCurrent) {
        this.maximumCurrent.set(maximumCurrent);
    }
     
    /**
     * Whether the cable is broken or not
     */
    private ReadOnlyBooleanWrapper broken = new ReadOnlyBooleanWrapper(false) {
        @Override
        public void set(boolean value) {
            if (value) { // isBroken(). In tick() this is propageted throught the entire tree
                setCurrent(0);
            }
            super.set(value);
        }
    };
    
    public ReadOnlyBooleanProperty brokenProperty() {
        return broken;
    }
    
    public final boolean isBroken() {
        return brokenProperty().get();
    }
    
    private void setBroken(boolean value) {
        broken.set(value);
    }
    
    /**
     * 
     * @return the node that is the child
     */
    public Node getChildNode() {
        return this.childNode;
    }

    public void tick(double time, boolean connected) {
        // if this cable is broken, the network behind it shouldn't do anything so the disconnected value is propagated
        if (this.isBroken()){
            connected = false;
        }
        getChildNode().tick(time, connected);
    }
    
    @Override
    public double doForwardBackwardSweep(double v) {
       //update the voltages in the forward sweep
        double voltage = v - (getCurrent() * (resistance * length));

        setCurrent(getChildNode().doForwardBackwardSweep(voltage));
        
        return getCurrent();
    }

    @Override
    public void reset() {
        this.setCurrent(0d);
    }
    
    public String toString(int indentation){
        String output = "";
        for (int i = 0; i < indentation; i++){
            output += "\t";
        }
        output += "|-";
        
        output += "(Cable:R="+ resistance +  ",I="+ this.getCurrent() + ") -> " + this.getChildNode().toString(indentation);
        return output;
    }
}

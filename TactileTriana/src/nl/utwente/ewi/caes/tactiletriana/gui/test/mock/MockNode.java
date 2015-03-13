/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.test.mock;

import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.CableBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.HouseBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.NodeBase;

/**
 * Mocks an INode, sweeps voltage from 200 to 260 and back
 * @author Richard
 */
public class MockNode extends NodeBase {
        
    public MockNode() {
        new AnimationTimer() {
            long last = -1;
            double direction = 1;

            @Override
            public void handle(long now) {
                if (last == -1) {
                    last = now;
                } else if (now - last > 100000000) {
                    last = now;
                    voltageProperty.set(getVoltage() + direction);
                    if (getVoltage() > 260) direction = -1;
                    if (getVoltage() < 200) direction = 1;
                }
            }

        }.start();
    }

    DoubleProperty voltageProperty = new SimpleDoubleProperty(200);

    @Override
    public ReadOnlyDoubleProperty voltageProperty() {
        return voltageProperty;
    }

    @Override
    public Set<CableBase> getCables() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HouseBase getHouse() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
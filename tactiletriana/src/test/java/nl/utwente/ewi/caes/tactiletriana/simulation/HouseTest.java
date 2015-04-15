/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Richard
 */
public class HouseTest {

    /**
     * Test of currentConsumptionProperty method, of class House.
     */
    @Test
    public void testCurrentConsumptionPropertyNoDevices() {
        System.out.println("currentConsumptionPropertyNoDevices");
        House instance = new House(null);
        
        ReadOnlyDoubleProperty result = instance.currentConsumptionProperty();
        
        assertEquals(0.0, result.get(), 0.01);
    }
    
    /**
     * Test of currentConsumptionProperty method, of class House.
     */
    @Test
    public void testCurrentConsumptionPropertyMultipleDevices() {
        System.out.println("currentConsumptionPropertyMultipleDevices");
        
        // Mock simulation
        Simulation simulation = mock(Simulation.class);
        when(simulation.currentTimeProperty()).thenReturn(new SimpleObjectProperty<>(LocalDateTime.of(2014, 1, 1, 0, 0)));
        
        House instance = new House(simulation);
        int nDevices = 5;
        double deviceConsumption = 50.0;
        for (int i = 0; i < nDevices; i++) {
            // Mock devices
            DeviceBase device = mock(DeviceBase.class);
            when(device.currentConsumptionProperty()).thenReturn(new SimpleDoubleProperty(deviceConsumption));
            instance.getDevices().add(device);
        }
        
        instance.tick(simulation, true);
        ReadOnlyDoubleProperty result = instance.currentConsumptionProperty();
        
        assertEquals(nDevices * deviceConsumption, result.get(), 0.01);
    }
    
}

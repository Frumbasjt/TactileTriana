/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.*;

/**
 * Base class for TimeShiftables.
 * 
 * Has the following properties as specified in the API:
 * <ul>
 *  <li>startTimes</li>
 *  <li>endTimes</li>
 * </ul>
 * 
 * Note that the start and end times are in fact not lists, but doubles, because
 * the simulation doesn't support multiple start end end times as of yet.
 */
public abstract class TimeShiftableBase extends DeviceBase {
        
    private final double[] profile;    // The program of this device (power consumption for every minute)
    private int currentMinute = 0;     // The step in the program at which the device currently is
    private boolean active;            // The device may now do its program
    private boolean programRemaining;  // Whether the device still has a program to do for the day
    
    /**
     * Constructs a new TimeShiftableBase. Registers the {@code startTimes} and
     * {@code endTimes} properties as specified in the API.
     * 
     * @param simulation    The Simulation this device belogns to
     * @param displayName   The name of the device as shown to the user
     * @param profile       The consumption profile of the device
     */
    public TimeShiftableBase(Simulation simulation, String displayName, double[] profile) {
        super(simulation, displayName, "TimeShiftable");
        
        // Ugly construction that is necessary to convert delay to endtime,
        // while still being able to bind both ways
        DoubleProperty endTime = new SimpleDoubleProperty();
        DoubleBinding endTimeBinding = Bindings.createDoubleBinding(() -> (getStartTime() + getDelay()) % 24*60, startTime, delay);
        endTimeBinding.addListener(obs -> endTime.set(endTimeBinding.get()));
        endTime.addListener(obs -> { 
            double delayValue = endTime.get() - startTime.get();
            delayValue = (delayValue >= 0) ? delayValue : delayValue + 24 * 60;
            setDelay(delayValue);
        });
        
        // register properties
        addProperty("startTimes", startTime);
        addProperty("endTimes", endTime);
        
        this.profile = profile;
        this.programRemaining = true;
        setDelay(profile.length);
    }
    
    // TODO: must become a (read-only) Property object
    
    /**
     * 
     * @return the program of this device
     */
    public double[] getProgram() {
        return this.profile;
    }
    
    /**
     * The time (in minutes from the start of the day) from which point the device may start operating
     */
    private final DoubleProperty startTime = new SimpleDoubleProperty();
    
    public DoubleProperty startTimeProperty() {
        return startTime;
    }
    
    public double getStartTime() {
        return startTimeProperty().get();
    }

    public void setStartTime(double start) {
        startTimeProperty().set(start);
    }
    
    
    /**
     * Amount of time (in minutes) that the TimeShiftable may delay its operation
     */
    protected final DoubleProperty delay = new SimpleDoubleProperty() {
        @Override
        public void set(double value) {
            if (value < 0) {
                throw new IllegalArgumentException("Delay may not be negative");
            }
            if (value > (24*60 - profile.length)) {
                throw new IllegalArgumentException("Delay may not be more than a full day minus the program length");
            }
            
            super.set(value);
        }
    };
    
    public DoubleProperty delayProperty() {
        return delay;
    }
    
    public final double getDelay() {
        return delay.get();
    }

    public final void setDelay(double timewindow) {
        this.delay.set(timewindow);
    }
    
    @Override
    public void tick (double timePassed, boolean connected){
        super.tick(timePassed, connected);
        
        double consumption = 0;
        
        IController controller = getSimulation().getController();
        LocalDateTime currentDateTime = getSimulation().getCurrentTime();
        if (controller != null && controller.getPlannedConsumption(this, currentDateTime) != null) {
            consumption = controller.getPlannedConsumption(this, currentDateTime);
        } else { // No planning available
            double currentTime = currentDateTime.getHour() * 60 + currentDateTime.getMinute();
            
            if (currentTime - timePassed < 0) {
                programRemaining = true;
            }
            
            // If not done for this period, check if we may start
            if (!active && programRemaining) {
                if (currentTime > getStartTime() || 
                        // Relevant if start time starts somewhere at the end of the day
                        currentTime - timePassed <= 0) {
                    active = true;
                }
            }
            
            // If active, consume energy until done
            if (active) {
                consumption = 0;
                for (int i = 0; i < SimulationConfig.SIMULATION_TICK_TIME; i++) {
                    consumption += profile[currentMinute];
                    currentMinute++;
                    if (currentMinute >= profile.length) {
                        currentMinute = 0;
                        active = false;
                        programRemaining = false;
                        break;
                    }
                }
                consumption = consumption / SimulationConfig.SIMULATION_TICK_TIME;
            }
        }
        
        setCurrentConsumption(consumption);
    }
    
}



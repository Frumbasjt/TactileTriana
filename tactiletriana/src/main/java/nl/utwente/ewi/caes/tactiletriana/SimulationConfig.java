/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

/**
 *
 * @author Mick
 */
public final class SimulationConfig {

    public static final int HOUSE_MAX_FUSE_CURRENT = 3 * 35;

    //public static final int SIMULATION_NUMBER_OF_HOUSES = 6;   // number of houses
    public static final int TICK_MINUTES = 5;   // time in minutes that passes in the simulation with each tick
    public static final int SYSTEM_TICK_TIME = 200;        // time between ticks in ms
    public static final boolean SIMULATION_UNCONTROLABLE_LOAD_ENABLED = true; // staat de uncontrolable load aan?

    
}

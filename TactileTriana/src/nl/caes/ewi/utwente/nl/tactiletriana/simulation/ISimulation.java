/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

/**
 *
 * @author Richard
 */
public interface ISimulation {
    /**
     * 
     * @return the rootnode of the network
     */
    public Transformer getTransformer();
    
    /**
     * Starts the simulation
     */
    public void start();
    
    /**
     * Pauses the simulation
     */
    public void stop();
}
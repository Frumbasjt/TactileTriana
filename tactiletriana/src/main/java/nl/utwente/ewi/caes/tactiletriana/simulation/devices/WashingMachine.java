/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author niels
 */
public class WashingMachine extends TimeShiftable{        

    public WashingMachine(Simulation simulation){
        super(simulation, "WashingMachine");
        this.programUsage = washingMachineUsage;
    }
    
    //usage of a washing machine program in 1m steps in W
    private final double[] washingMachineUsage = {
        66.229735,
        119.35574,
        162.44595,
        154.744551,
        177.089979,
        150.90621,
        170.08704,
        134.23536,
        331.837935,
        2013.922272,
        2032.267584,
        2004.263808,
        2023.32672,
        2041.49376,
        2012.8128,
        2040.140352,
        1998.124032,
        2023.459776,
        1995.309312,
        2028.096576,
        1996.161024,
        552.525687,
        147.718924,
        137.541888,
        155.996288,
        130.246299,
        168.173568,
        106.77933,
        94.445568,
        130.56572,
        121.9515,
        161.905679,
        176.990625,
        146.33332,
        173.06086,
        145.07046,
        188.764668,
        88.4058,
        117.010432,
        173.787341,
        135.315969,
        164.55528,
        150.382568,
        151.517898,
        154.275128,
        142.072704,
        171.58086,
        99.13293,
        94.5507,
        106.020684,
        194.79336,
        239.327564,
        152.75808,
        218.58576,
        207.109793,
        169.5456,
        215.87571,
        186.858018,
        199.81808,
        108.676568,
        99.930348,
        151.759998,
        286.652289,
        292.921008,
        300.5829,
        296.20425,
        195.74251,
        100.34136,
        312.36975,
        287.90921,
        85.442292,
        44.8647
    };

}
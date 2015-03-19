/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.deviceconfig;

import java.util.List;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase.Parameter;

/**
 *
 * @author Richard
 */
public class DeviceConfigVM {
    private final List parameters;
    
    public DeviceConfigVM(List<Parameter> deviceParameters) {
        this.parameters = deviceParameters;
    }
    
    /**
     * @return the list of parameters that can be configured
     */
    public List<Parameter> getParameters() {
        return parameters;
    }
}

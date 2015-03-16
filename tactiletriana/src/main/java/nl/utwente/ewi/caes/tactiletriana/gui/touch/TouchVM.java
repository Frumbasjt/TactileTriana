/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch;

import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.node.NodeVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.transformer.TransformerVM;
import nl.utwente.ewi.caes.tactiletriana.simulation.CableBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Node;
import nl.utwente.ewi.caes.tactiletriana.simulation.NodeBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.SimulationBase;

/**
 *
 * @author Richard
 */
public class TouchVM {
    private final SimulationBase model;
    
    private final TransformerVM transformer;
    private final NodeVM internalNodes[];
    private final NodeVM houseNodes[];
    private final CableVM internalCables[];
    private final CableVM houseCables[];
    private final HouseVM houses[];
    
    public TouchVM(SimulationBase model) {
        this.model = model;
        
        this.transformer = new TransformerVM(model.getTransformer());
        this.internalNodes = new NodeVM[6];
        this.houseNodes = new NodeVM[6];
        this.internalCables = new CableVM[6];
        this.houseCables = new CableVM[6];
        this.houses = new HouseVM[6];
        
        internalCables[0] = new CableVM(model.getTransformer().getCables().get(0));
        NodeBase node = model.getTransformer().getCables().get(0).getChildNode();
        for (int i = 0; i < 6; i++) {
            internalNodes[i] = new NodeVM(node);
            for (CableBase cable: node.getCables()) {
                NodeBase childNode = cable.getChildNode();
                if (childNode.getHouse() != null) {
                    houseNodes[i] = new NodeVM(childNode);
                    houseCables[i] = new CableVM(cable);
                    houses[i] = new HouseVM(childNode.getHouse());
                } else {
                    if (i < 5) {
                        internalCables[i + 1] = new CableVM(cable);
                    }
                    node = childNode;
                }
            }
        }
    }
    
    public TransformerVM getTransformer() {
        return transformer;
    }
    
    public NodeVM[] getInternalNodes() {
        return internalNodes;
    }
    
    public NodeVM[] getHouseNodes() {
        return houseNodes;
    }

    public CableVM[] getInternalCables() {
        return internalCables;
    }

    public CableVM[] getHouseCables() {
        return houseCables;
    }

    public HouseVM[] getHouses() {
        return houses;
    }
    
    
}

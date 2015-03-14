/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseView;

/**
 *
 * @author Richard
 */
public class NetworkView extends Pane {
    public NetworkView() {
        ViewLoader.load(this);
        
        // Move all cables to the background (they're just in the foreground at first because that works better with scene builder)
        List<Node> cables = new ArrayList<>();
        getChildren().stream().filter((node) -> (node instanceof CableView)).forEach(cables::add);
        cables.stream().forEach(Node::toBack);
    }
    
    public List<HouseView> getHouseViews() {
        List<HouseView> result = new ArrayList<>();
        getChildren().stream().filter(node -> node instanceof HouseView).forEach(node -> result.add((HouseView) node));
        return result;
    }
}

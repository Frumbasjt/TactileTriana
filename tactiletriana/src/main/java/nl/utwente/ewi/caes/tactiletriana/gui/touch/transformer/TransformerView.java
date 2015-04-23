/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.transformer;

import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Rectangle;
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.events.TrianaEvents;

/**
 *
 * @author Richard
 */
public class TransformerView extends Rectangle {
    private TransformerVM viewModel;
    
    public TransformerView() {
        ViewLoader.load(this);
    }
    
    public void setViewModel(TransformerVM viewModel) {
        this.viewModel = viewModel;
        
        // Show entire network on chart on long press
        TrianaEvents.addShortAndLongPressEventHandler(this, null, n -> {
            viewModel.longPressed();
        });
        
        viewModel.shownOnChartProperty().addListener(obs -> {
            if (viewModel.isShownOnChart()) {
                this.setEffect(new DropShadow());
            } else {
                this.setEffect(null);
            }
        });
    }
}

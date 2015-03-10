/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;

/**
 * A connection between to nodes
 */
public interface ICable {
    /**
     * 
     * @return the current flowing through the cable
     */
    public ReadOnlyDoubleProperty currentProperty();
    
    /**
     * 
     * @return the absolute maximum current that can flow through the cable before it breaks
     */
    public ReadOnlyDoubleProperty maximumCurrentProperty();
    
    /**
     * 
     * @return whether the cable is broken or not
     */
    public ReadOnlyBooleanProperty brokenProperty();
    
    /**
     * 
     * @return the node that is the parent
     */
    public INode getNodeParent();
    /**
     * 
     * @return the node that is the child
     */
    public INode getNodeChild();
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.da.twilight.leyla.ui.support;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author ShadowWalker
 */
public class WindowsDecorationBar extends ToolBar  {
    
    private final ImageView icon = new ImageView();
    
    public WindowsDecorationBar(){
        this.setStyle("-fx-background-color: #5b92ff;");
        int toolBarHeight = 26;
        this.setPrefHeight( toolBarHeight );
        this.setMinHeight( toolBarHeight );
        this.setMaxHeight( toolBarHeight );
        this.setPadding( new Insets(0, 0, 0, 0) );

        Label smallSpace = new Label("  ");
        
        icon.setFitHeight( 18.0 );
        icon.setFitWidth( 18.0 );
        
        Label title = new Label("Leyla - File management system");
        title.setStyle("-fx-text-fill:#fff; -fx-font-size: 14;");
        title.setPadding(new Insets(5, 10, 5, 10));
        
        HBox emptySpace = new HBox(); // For let toolbar's conponents flow to right side of toolbar
        HBox.setHgrow(emptySpace, Priority.ALWAYS);

        this.getItems().addAll( smallSpace, icon, title, emptySpace, new DecorationButtons() );
        
        makeThisDraggable();
    }

    public void makeThisDraggable(){
        addDraggableNode( this );
    }
    
    public void setIcon(Image icon){
        this.icon.setImage(icon); 
    }
    
    /**
     * Add draggable function to customized toolbar 
     */
    class Delta { double initialX, initialY; }  // coordinate data holder

    final Delta dragDelta = new Delta();

    private void addDraggableNode(final Node node) {
        node.setOnMousePressed((MouseEvent me) -> {
            if (me.getButton() != MouseButton.MIDDLE) {
                dragDelta.initialX = me.getSceneX();
                dragDelta.initialY = me.getSceneY();
            }
        });
        node.setOnMouseDragged((MouseEvent me) -> {
            if (me.getButton() != MouseButton.MIDDLE) {
                node.getScene().getWindow().setX(me.getScreenX() - dragDelta.initialX);
                node.getScene().getWindow().setY(me.getScreenY() - dragDelta.initialY);
            }
        });
    }
}
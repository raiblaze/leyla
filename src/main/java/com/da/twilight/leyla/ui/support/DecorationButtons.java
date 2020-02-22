/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.da.twilight.leyla.ui.support;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Customize Close, Max, Min buttons of application windows 
 * 
 * @author ShadowWalker
 */
class DecorationButtons extends HBox {

    public DecorationButtons() {
        double prefHeight = 30.0;
        
        Button minBtn = new Button("  🗕  ");
        minBtn.setPrefHeight( prefHeight );
        minBtn.setOnAction((evt) -> {
            ((Stage) this.getScene().getWindow()).setIconified(true);
        });

        Button maxBtn = new Button("  🗖  ");
        maxBtn.setPrefHeight( prefHeight );
        maxBtn.setOnAction((evt) -> {
            Stage stage = ((Stage) this.getScene().getWindow());
            stage.setMaximized( !stage.isMaximized() );
        });

        Button closeBtn = new Button("  ✕  ");
        closeBtn.setPrefHeight( prefHeight );
        closeBtn.setOnAction((evt) -> {
            ((Stage) this.getScene().getWindow()).close(); // <-- Make UI seem to be response faster and not stuck when clik button
            Platform.exit();
        });

        this.getChildren().addAll(minBtn ,maxBtn, closeBtn);
    }
}
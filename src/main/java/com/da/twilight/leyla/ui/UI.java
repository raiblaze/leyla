/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.da.twilight.leyla.ui;


import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 
 * @author ShadowWalker
 */
public class UI extends Application{

    
    /**
     * Start primary stage of JavaFX UI
     * 
     * @param primaryStage
     * @throws IOException 
     */
    @Override
    public void start(Stage primaryStage) throws IOException{
        
        Parent root = FXMLLoader.load(getClass().getResource("fxml/FileFXML.fxml"));

        //Scene scene = new Scene(root);
        primaryStage.setTitle("Leyla - file search");

        /* Don't use normal close, maximize, minimize button of windows */
        primaryStage.initStyle(StageStyle.UNDECORATED);
        BorderPane borderPane = new BorderPane();
        ToolBar toolBar = new ToolBar();


        int height = 25;
        toolBar.setPrefHeight(height);
        toolBar.setMinHeight(height);
        toolBar.setMaxHeight(height);

        HBox hbox = new HBox();
        toolBar.setPadding(new Insets(0, 0, 0, 0));
        HBox.setHgrow(hbox, Priority.ALWAYS);
        toolBar.getItems().add(hbox);
        addDraggableNode(toolBar);
        WindowButtons wb = new WindowButtons();
        wb.setStage(primaryStage);

        toolBar.getItems().add(wb);

        borderPane.setTop(toolBar);
        borderPane.setBottom(root);

        primaryStage.setScene(new Scene(borderPane));

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("image/search-icon.png")));
        // Need to set this handler to prevent some background thread still running while UI thread is closed. => force close entire program when UI close
        //primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }
    
    
    /**
     * Customize Close, Max, Min buttons of application windows 
     **/
    class WindowButtons extends HBox {
        public WindowButtons() {
            Button minBtn = new Button("_");
            minBtn.setOnAction((evt) -> {
                stage.setIconified(true);
            });
            
            
            Button maxBtn = new Button("0");
            maxBtn.setOnAction((evt) -> {
                stage.setMaximized( !stage.isMaximized() );
            });
            
            Button closeBtn = new Button("X");
            closeBtn.setOnAction((evt) -> {
                Platform.exit();
            });
            
            this.getChildren().add(minBtn);
            this.getChildren().add(maxBtn);
            this.getChildren().add(closeBtn);
            
        }
        private Stage stage;
        public void setStage(Stage stage){
            this.stage = stage;
        }
    }
    
    
    /**
     * Add draggable function to customized toolbar 
    **/
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
    
    
    /**
     * Force to close entire program when UI Thread is close 
     * Because may be some background task on another thread still running 
     * This Event Handler use for that case
     */
    @Override
    public void stop(){
        System.out.println("App is closing");
        System.exit(0);
    }
    
    /**
     * Main method
     * 
     * @param args 
     */
    public static void main(String[] args) {
        launch(args);
    } 
}

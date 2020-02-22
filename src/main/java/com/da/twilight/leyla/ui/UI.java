/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.da.twilight.leyla.ui;


import com.da.twilight.leyla.ui.support.WindowsDecorationBar;
import java.io.IOException;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
        Parent root = FXMLLoader.load(getClass().getResource( "fxml/FileFXML.fxml") );

        // Scene scene = new Scene(root);
        // primaryStage.getIcons().add( new Image( UI.class.getResourceAsStream("image/search-icon.png") ));
        // primaryStage.setTitle("Leyla - file search");
        
        /* Don't use normal close, maximize, minimize button of windows */
        primaryStage.initStyle(StageStyle.UNDECORATED);
        BorderPane borderPane = new BorderPane();
        
        WindowsDecorationBar windowsDecorationBar = new WindowsDecorationBar();
        windowsDecorationBar.setIcon( new Image( UI.class.getResourceAsStream("image/search-icon.png") ) );

        borderPane.setTop( windowsDecorationBar );
        borderPane.setBottom( root );
        
        // Fix not Resize problem of content AnchorPane. Force changing when borderPane height change
        AnchorPane anchor = ((AnchorPane)root);
        borderPane.heightProperty().addListener(
                (ObservableValue<? extends Number> ov, Number t, Number t1) -> {
                    anchor.prefHeightProperty()
                            .set( t1.doubleValue() - windowsDecorationBar.getHeight() ); // - 25 for toolbar header
        });
        
        primaryStage.setScene(new Scene( borderPane ));
        
        // Need to set this handler to prevent some background thread still running while UI thread is closed. => force close entire program when UI close
        //primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
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

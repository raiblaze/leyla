/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.da.twilight.leyla.ui.support;

import com.da.twilight.leyla.ui.UI;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Customize Close, Max, Min buttons of application windows 
 * 
 * @author ShadowWalker
 */
class DecorationButtons extends HBox {

    private final Logger LOGGER = LoggerFactory.getLogger( DecorationButtons.class );
    
    private TrayIcon trayIcon;
    
    public DecorationButtons() {
        double prefHeight = 26.0;
        
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
            LOGGER.debug("Closing application window");
            
            //Once the JavaFX toolkit has started, by default it will close down when the last visible window is closed. To prevent this, you can call
            Platform.setImplicitExit(false);
            
            ((Stage) this.getScene().getWindow()).hide(); // <-- Make UI seem to be response faster and not stuck when clik button
        });
        
        createSystemTray();

        this.getChildren().addAll(minBtn ,maxBtn, closeBtn);
    }
    
    /**
     * Create Tray System Icon
     */
    private void createSystemTray(){
        if (!SystemTray.isSupported()) {
            LOGGER.info("SystemTray is not supported");
            return;
        }
        
        final PopupMenu popup = new PopupMenu();
        this.trayIcon = new TrayIcon(createImage("image/search-icon.png", "App's running"));
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if(me.getClickCount() >= 2){
                    //setVisible(true);
                    //((Stage) this.getScene().getWindow())
                    LOGGER.debug("Showing application window");
                    Platform.runLater(()-> {
                        ((Stage) DecorationButtons.this.getScene().getWindow()).show();
                    });
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {}

            @Override
            public void mouseReleased(MouseEvent me) {}

            @Override
            public void mouseEntered(MouseEvent me) {}

            @Override
            public void mouseExited(MouseEvent me) {}

        });
        final SystemTray tray = SystemTray.getSystemTray();
        
        // Create a pop-up menu components
        MenuItem aboutItem = new MenuItem("About");
        MenuItem cb1 = new MenuItem("Window");
        cb1.addActionListener((ActionEvent ae) -> {
            ((Stage) DecorationButtons.this.getScene().getWindow()).show();
        });
        
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener((ActionEvent ae) -> {
            Platform.exit();
        });
       
        //Add components to pop-up menu
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(cb1);
        popup.addSeparator();
        popup.add(exitItem);
       
        trayIcon.setPopupMenu(popup);
       
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            LOGGER.error("TrayIcon could not be added.");
        }
        
    }
    
    protected Image createImage(String path, String description) {
        URL imageURL = UI.class.getResource(path);
        if (imageURL == null) {
            LOGGER.error("Resource not found: ", path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}
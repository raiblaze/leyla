/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.da.twilight.leyla.ui.fxml;



import com.da.twilight.leyla.components.FileWatcher;
import com.da.twilight.leyla.components.Finder;
import com.da.twilight.leyla.ui.Loggable;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author ShadowWalker
 */
public class FileFXMLController implements Initializable, Loggable {

    @FXML
    private Button searchBtn;
    @FXML
    private Button watchBtn;
    @FXML
    private Button stopWatchBtn;
    @FXML
    private TextArea searchTxtarea;
    @FXML
    private TextField searchTxt;

    private Thread watcherTask;
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        searchBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                searchTxtarea.clear();
                String searchStr = searchTxt.getText();
                Finder finder = new Finder("*" + searchStr + "*");
                finder.setLogger(FileFXMLController.this);
                try {
                    Files.walkFileTree( Paths.get("."), finder);
                } catch (IOException ex) {
                    Logger.getLogger(FileFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
                finder.done();
            }
        });
        
        watchBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileFXMLController.this.watchBtn.setDisable(true);
                FileWatcher fw = new FileWatcher();
                fw.setFolder(".");
                fw.setLogger(FileFXMLController.this);
                
                watcherTask = new Thread(fw);
                watcherTask.start();
            }
        });
        
        stopWatchBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                watchBtn.setDisable(false);
                if(watcherTask != null){
                    watcherTask.interrupt();
                }
            }
        });
        
        // select line of text area when click
        searchTxtarea.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {

                    // check, if click was inside the content area
                    Node n = event.getPickResult().getIntersectedNode();
                    while (n != searchTxtarea) {
                        if (n.getStyleClass().contains("content")) {
                            // find previous/next line break
                            int caretPosition = searchTxtarea.getCaretPosition();
                            String text = searchTxtarea.getText();
                            int lineBreak1 = text.lastIndexOf('\n', caretPosition - 1);
                            int lineBreak2 = text.indexOf('\n', caretPosition);
                            if (lineBreak2 < 0) {
                                // if no more line breaks are found, select to end of text
                                lineBreak2 = text.length();
                            }

                            searchTxtarea.selectRange(lineBreak1, lineBreak2);
                            event.consume();
                            break;
                        }
                        n = n.getParent();
                    }
                }
            }
        });
    }
    
    public void appendLog(String str){
        searchTxtarea.appendText(str+"\n");
    }

    @Override
    public void log(String msg) {
        Platform.runLater(() -> {
            appendLog(msg);
        });
    }
}

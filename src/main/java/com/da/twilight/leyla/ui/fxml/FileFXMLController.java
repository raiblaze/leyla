/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.da.twilight.leyla.ui.fxml;



import com.da.twilight.leyla.components.FileWatcher;
import com.da.twilight.leyla.components.Finder;
import com.da.twilight.leyla.components.Player;
import com.da.twilight.leyla.components.TreeNode;
import static com.da.twilight.leyla.components.TreeNode.createDirTree;
import static com.da.twilight.leyla.components.TreeNode.renderDirectoryTree;
import com.da.twilight.leyla.ui.Loggable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * FXML Controller class
 *
 * @author ShadowWalker
 */
public class FileFXMLController implements Initializable, Loggable {

    private Logger LOGGER = LoggerFactory.getLogger( FileFXMLController.class );
    
    @FXML
    private Button searchBtn;
    @FXML
    private Button watchBtn;
    @FXML
    private Button stopWatchBtn;
    @FXML
    private Button printTreeBtn;
    @FXML
    private TextArea searchTxtarea;
    @FXML
    private TextArea errTxtarea;
    @FXML
    private TextField searchTxt;
    @FXML
    private ComboBox searchLocationCb;
    
    private Properties prop;
    
    private Thread watcherTask;
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            prop = new Properties();
            prop.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException ex) {
            errTxtarea.appendText("Can't load application properties file!");
            errTxtarea.appendText("Error: " + ex.toString());
        }
        
        //String workingDir = prop.getProperty("working.path");
        searchBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                searchTxtarea.clear();
                String searchStr = searchTxt.getText().trim();
                Finder finder = new Finder("*" + searchStr + "*");
                finder.setLogger(FileFXMLController.this);
                try {
                    Files.walkFileTree( Paths.get( getCurrentDir() ), finder);
                } catch (IOException ex) {
                    LOGGER.error("Error while walking tree err={}",ex.toString());
                }
                finder.done();
            }
        });
        
        // WATCHING SERVICE BUTTON for logging file change on console
        watchBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileFXMLController.this.watchBtn.setDisable(true);
                FileWatcher fw = new FileWatcher();
                fw.setFolder( getCurrentDir() );
                fw.setLogger( FileFXMLController.this );
                
                watcherTask = new Thread(fw);
                watcherTask.setDaemon(true);   // this line for deamon app. when app's closed. this task running on another thread close too 
                watcherTask.start();
            }
        });
        // STOP WATCHING SERVICE BUTTON
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
                            
                            // open media file for mp4 because JFX only support mp4 container
                            if(searchTxtarea.getSelectedText().trim().contains(".mp4")){
                                Stage stage = new Stage();
                                File file = new File( searchTxtarea.getSelectedText().trim() );

                                Player player = new Player( file.toURI().toString());
                                
                                // Adding player to the Scene 
                                Scene scene = new Scene(player, 1920/2, 1080/2, Color.BLACK);
                                stage.setScene(scene);  
                                stage.setTitle("Playing video");  
                                stage.show(); 
                                
                                stage.setOnCloseRequest(evt -> {
                                    player.pause();
                                    LOGGER.debug("Player is closing");
                                });
                            }
                            
                            event.consume();
                            break;
                        }
                        n = n.getParent();
                    }
                }
            }
        });
        
        printTreeBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                File file = new File( getCurrentDir() );
                TreeNode<File> DirTree = createDirTree(file);
                String result = renderDirectoryTree(DirTree);
                log(result);
                /*
                try {
                    BufferedWriter bw = Files.newBufferedWriter( Paths.get("D:\\tree.txt"), StandardCharsets.UTF_16, StandardOpenOption.WRITE);
                    bw.write(result);
                } catch (IOException iox) {
                    System.out.println("[FAILED] Error: " + iox.toString());
                } 
                */
            }
        });
        
        searchLocationCb.getItems().addAll(
                "C:\\Users\\ShadowWalker\\Downloads",
                "H:\\"
        );
        searchLocationCb.setVisibleRowCount(4);
        searchLocationCb.setValue("H:\\");
        
        /* Roll-back to old value when value change to new value */
        /*searchLocationCb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal)->{
            if(newVal != null && !newVal.equals("Two")){
                Platform.runLater(() -> searchLocationCb.setValue(oldVal));
            }
        }); */
        
    }
    
    public String getCurrentDir(){
        return searchLocationCb.getValue().toString();
    }
    
    @Override
    public void log(String msg) {
        Platform.runLater(() -> {
            searchTxtarea.appendText(msg + "\n");
        });
    }

    @Override
    public void err(String msg) {
        Platform.runLater(() -> {
            errTxtarea.appendText(msg + "\n");
        });
    }
}

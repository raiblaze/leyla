/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.da.twilight.leyla.components;

import javafx.scene.layout.BorderPane; 
import javafx.scene.layout.Pane; 
import javafx.scene.media.Media; 
import javafx.scene.media.MediaPlayer; 
import javafx.scene.media.MediaView; 
/**
 *
 * @author ShadowWalker
 */
public class Player extends BorderPane {// Player class extend BorderPane 
    // in order to divide the media 
    // player into regions 
    
    private final Media media; 
    private final MediaPlayer player; 
    private final MediaView view; 
    private final Pane mpane; 
    private final MediaBar bar; 

    public Player(String file) {
        media = new Media(file); 
        player = new MediaPlayer(media); 
        view = new MediaView(player); 
        mpane = new Pane(); 
        mpane.getChildren().add(view); // Calling the function getChildren 

        // inorder to add the view 
        setCenter(mpane); 
        bar = new MediaBar(player); // Passing the player to MediaBar 
        setBottom(bar); // Setting the MediaBar at bottom 
        setStyle("-fx-background-color:#bfc2c7"); // Adding color to the mediabar 
        player.play(); // Making the video play 
    } 
    
    public void pause(){
        player.pause();
        player.dispose();
    }
} 

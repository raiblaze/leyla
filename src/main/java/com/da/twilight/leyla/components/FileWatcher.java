/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.da.twilight.leyla.components;

import com.da.twilight.leyla.ui.Loggable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;


/**
 *
 * @author ShadowWalker
 */
public class FileWatcher implements Runnable{
    private String folder =  ".";

    public void watchOffers() {
        WatchService watchService;
        try {
            watchService = FileSystems.getDefault().newWatchService();

            //listen for create ,delete and modify event kinds of folder and all subfolders
            Files.walkFileTree(Paths.get(folder), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException{
                    dir.register(watchService, 
                            ENTRY_CREATE, 
                            ENTRY_DELETE, 
                            ENTRY_MODIFY);
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    logger.err("Failed for trying to monitor file/dir:" + file);
                    return FileVisitResult.SKIP_SUBTREE;
                } 
            });
        } catch(IOException ioe){
            logger.err("Error: " + ioe.toString());
            return ;
        }
        
        // listening events from paths
        while(true) {
            WatchKey key;
            try {
                //return signaled key, meaning events occurred on the object
                key = watchService.take();
            } catch (InterruptedException ex) {
                return; // exit this loop when thread is interupted
            }

            //retrieve all the accumulated events
            key.pollEvents().stream().forEach((event) -> {
                WatchEvent.Kind<?> kind = event.kind();

                Path path = (Path) event.context();
                logger.log("[" + kind.name() +"]  " + path.toAbsolutePath().toString());

                if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                    readOffer(path.toString());
                }
            });             
            //resetting the key goes back ready state
            key.reset();
        }
    }
    private void writeOffer(String fileName, String offer) {
        Path filePath = Paths.get(folder + fileName);		

        ByteBuffer bb = ByteBuffer.wrap(offer.getBytes());

        try (FileChannel fc = (FileChannel.open(filePath,StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING))) {
                bb.rewind();
                fc.write(bb);
        } catch (IOException ex) {
            logger.err("exception in writing to offers file: "+ex);
        }
    }

    private void readOffer(String fileName) {
        Path filePath = Paths.get(folder + fileName);

        ByteBuffer bb = ByteBuffer.allocate(100);

        Charset cset =  Charset.forName("UTF-8");
        StringBuilder sb = new StringBuilder();
        String off;
        try (FileChannel fc = (FileChannel.open(filePath,
                        StandardOpenOption.READ))) {

            while (fc.read(bb) > 0) {
                    bb.rewind();
                    off  = cset.decode(bb).toString();
                    sb.append(off);
                    
                    logger.log(off);
                    
                    bb.flip();
            }
        } catch (IOException ex) {
            logger.err("exception in reading file: " + ex);
        }
    }

    private Loggable logger;
    public void setLogger(Loggable logger){
        this.logger = logger;
    }

    public void setFolder(String folder){
        this.folder = folder;
    }
    
    @Override
    public void run() {
        this.watchOffers();
    }
}
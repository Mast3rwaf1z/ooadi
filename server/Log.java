package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import server.events.Event;

public class Log implements Serializable{
    private String path = "logs/log.log";
    
    public void add(Event event) throws IOException{
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path), true))){
            writer.write(event.build());
            writer.newLine();
            writer.flush();
        }
        
    }

    public Event get(int timestamp){
        return null;
    }

    public void clear() throws IOException {
        try(FileWriter writer = new FileWriter(new File(path))){
            writer.write("");
            writer.flush();
        }
    }
}

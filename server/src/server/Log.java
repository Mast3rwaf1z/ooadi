package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import server.events.Event;

public class Log{
    private String path = "server/files/log.log";
    
    public void add(Event event){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path), true))){
            writer.write(event.build());
            writer.newLine();
            writer.flush();
        }
        catch(IOException e){
            Server.getCli().printException(e);
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

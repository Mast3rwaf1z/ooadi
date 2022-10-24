package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

public class Log implements Serializable{
    private String path = "logs/log.log";
    
    public void add(Event event){
    }

    public Event get(int timestamp){
        return null;
    }

    public void clear() throws IOException {
        try(FileWriter writer = new FileWriter(new File(path))){
            writer.write("1");
            writer.flush();
        }
    }
}

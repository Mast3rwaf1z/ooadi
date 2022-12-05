package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import server.events.Event;

/**
 * This is the logging class, it calls the Event objects and builds their logging message
 * @param add adds an event to the log file
 * @param clear clears the log, use with caution
 */
public class Log{
    private String path = "server/files/log.log";
    
    /**
     * This method adds an event to the end of the log file
     * @param event This is the event to be logged, as Event is an interface, this will be a class implementing Event
     */
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

    /**
     * This method clears the log, use with caution
     * @throws IOException
     */
    public void clear() throws IOException {
        try(FileWriter writer = new FileWriter(new File(path))){
            writer.write("");
            writer.flush();
        }
    }
}

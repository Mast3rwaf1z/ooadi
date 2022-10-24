package server;

import java.io.IOException;
import java.util.List;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import server.events.SensorAddEvent;
import server.events.SensorRemoveEvent;

public class CommandLineInterface implements Runnable{public static final String RESET = "\u001B[0m";
    private static final String BLACK = "\u001B[30;1m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    private LineReader reader = LineReaderBuilder.builder().build();
    private String prompt = "> ";

    public void run() {
        for(String line = ""; !line.equals("exit"); line = reader.readLine(prompt)){
            String[] args = line.split(" ");
            switch(args[0]){
                case "": break;
                case "add":
                    addSensor(args.length == 3 ? args[2] : "localhost", args.length > 1 ? args[1] : "0");
                    break;
                case "remove":
                    removeSensor(args.length > 1 ? args[1] : "0");
                    break;
                default:
                    reader.printAbove(RED+"No command was registered!"+BLACK+" commands: [add, remove, exit]"+RESET);
                    break;
                
            }

        }
    }

    private void addSensor(String addr, String id){
        try {
            Server.getLog().add(new SensorAddEvent(id, addr));
        } catch (IOException e) {
            printException(e);
        }

    }

    private void removeSensor(String id){
        try {
            Server.getLog().add(new SensorRemoveEvent(id));
        } catch (IOException e) {
            printException(e);
        }
        
    }

    public void debugPrint(String message){
        reader.printAbove(BLACK+message+RESET);
    }

    public void errorPrint(String message){
        reader.printAbove(RED+message+RESET);
    }

    public void acceptPrint(String message){
        reader.printAbove(GREEN+message+RESET);
    }

    public void print(String message){
        reader.printAbove(message);
    }

    public void printException(Exception e){
        reader.printAbove(e.toString());
        for(StackTraceElement element : e.getStackTrace()){
            reader.printAbove("\t"+element.toString());
        }
    }
    
}

package server;

import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import server.events.SensorAddEvent;
import server.events.SensorRemoveEvent;

public class CommandLineInterface implements Runnable{
    private static String os = System.getProperty("os.name");
    private static final String RESET   = os.equals("Linux") ? "\u001B[0m"      : "";
    private static final String BLACK   = os.equals("Linux") ? "\u001B[30;1m"   : "";
    private static final String RED     = os.equals("Linux") ? "\u001B[31m"     : "";
    private static final String GREEN   = os.equals("Linux") ? "\u001B[32m"     : "";
    private static final String CYAN    = os.equals("Linux") ? "\u001B[36m"     : "";

    private LineReader reader = LineReaderBuilder.builder().build();
    private String prompt = "> ";

    public CommandLineInterface(){
    }

    public void run() {
        for(String line = ""; !line.equals("exit"); line = reader.readLine(CYAN+prompt+RESET)){
            String[] args = line.split(" ");
            switch(args[0]){
                case "": break;
                case "add":
                    addSensor(args.length > 1 ? args[1] : "-1");
                    break;
                case "remove":
                    removeSensor(args.length > 1 ? args[1] : "-1");
                    break;
                case "clear":
                    Lock lock = Server.getCollectLock();
                    lock.lock();
                    if(confirm("Are you sure you want to clear the database? (y/N) ")){
                        Server.getDatabase().clear();
                        Server.clearSensors();
                        acceptPrint("Successfully cleared database");
                    }
                    lock.unlock();
                    break;
                case "adduser":
                    if(args.length == 3){
                        Server.getDatabase().addUser(args[1], args[2]);
                        acceptPrint("Successfully added user: "+args[1]);
                    }
                    else{
                        errorPrint("Error, not enough arguments: [adduser <username> <password>]");
                    }
                    break;
                case "removeuser":
                    if(args.length == 2){
                        Server.getDatabase().removeUser(args[1]);
                        acceptPrint("Successfully removed user: "+args[1]);
                    }
                    else{
                        errorPrint("Error, not enough arguments: [removeuser <username>]");
                    }
                    break;
                case "showdata":
                    if(args.length == 2){
                        Map<String, String> data = Server.getDatabase().getSensorData(args[1]);
                        for(String key : data.keySet()){
                            reader.printAbove(CYAN+key+": "+BLACK+data.get(key)+RESET);
                        }
                    }
                    else{
                        errorPrint("Error, not enough arguments: [showdata <sensorid>");
                    }
                    break;
                default:
                    reader.printAbove(RED+"No command was registered!"+BLACK+" commands: [add, remove, clear, adduser, removeuser, showdata, exit]"+RESET);
                    break;
                
            }

        }
        System.exit(0);
    }

    private void addSensor(String id){
        Server.getLog().add(new SensorAddEvent(id));
        Lock lock = Server.getCollectLock();
        lock.lock();
        Server.getDatabase().addEntry(id);
        lock.unlock();
        

    }

    private void removeSensor(String id){
        Server.getLog().add(new SensorRemoveEvent(id));
        Server.removeSensor(id);
        Server.getDatabase().removeEntry(id);
        
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

    public boolean confirm(String prompt){
        String result = reader.readLine(prompt);
        return result.equalsIgnoreCase("y") || result.equalsIgnoreCase("yes");
    }
    
}

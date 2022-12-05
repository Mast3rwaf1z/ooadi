package server;

import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import server.events.SensorRemoveEvent;

/**
 * This class is the command line interface (CLI) that runs in a seperate thread, it implements runnable so it can be used as a parameter to the Thread object in the class Server.java
 * @param run This is the supertype method run of the Runnable class, this is run when the threading object is started.
 * @param addSensor This method is the handler for the add command, it adds a sensor to the database
 * @param removeSensor This method is the handler for the remove command, it removes a sensor from the database 
 * @param debugPrint This method prints a gray message to show debugging messages, this is convenient as the printing line can be commented away later and thus removing all debugging messages in the terminal
 * @param errorPrint This method prints a red message, it is to show that something bad or important has happened
 * @param acceptPrint This method prints a green message, this shows that something good and important has happened.
 * @param print Prints a String, the reason a print method has to be defined is to keep the prompt at the bottom of the terminal
 * @param printException Prints an exception above the prompt
 * @param confirm asks the user for a string, a "y" or a "yes" returns a true while anything else returns a false
 */
public class CommandLineInterface implements Runnable{
    private static String os = System.getProperty("os.name");
    private static final String RESET   = os.equals("Linux") ? "\u001B[0m"      : "";
    private static final String BLACK   = os.equals("Linux") ? "\u001B[30;1m"   : "";
    private static final String RED     = os.equals("Linux") ? "\u001B[31m"     : "";
    private static final String GREEN   = os.equals("Linux") ? "\u001B[32m"     : "";
    private static final String CYAN    = os.equals("Linux") ? "\u001B[36m"     : "";

    private LineReader reader = LineReaderBuilder.builder().build();
    private String prompt = "> ";

    /**
     * This is the supertype method run, it has a loop that takes commands from the terminal window and responds to them
     */
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

    /**
     * This method is the handler for the add command. It is responsible for adding sensors to the database. It takes 1 parameter
     * @param id The id to be added to the database
     */
    private void addSensor(String id){
        Lock lock = Server.getCollectLock();
        lock.lock();
        Server.getDatabase().addEntry(id);
        lock.unlock();
        

    }

    /**
     * This method is the handler for the remove command. It is responsible for removing sensors from the database. It takes 1 parameter
     * @param id The id to be removed from the database
     */
    private void removeSensor(String id){
        Server.getLog().add(new SensorRemoveEvent(id));
        Server.removeSensor(id);
        Server.getDatabase().removeEntry(id);
        
    }

    /**
     * This is the debug print method, it prints a gray message above the prompt in the terminal
     * @param message The message to be printed
     */
    public void debugPrint(String message){
        reader.printAbove(BLACK+message+RESET);
    }

    /**
     * This is the error print method, it prints a red message above the prompt in the terminal
     * @param message The message to be printed
     */
    public void errorPrint(String message){
        reader.printAbove(RED+message+RESET);
    }

    /**
     * This is the accept print method, it prints a green message above the prompt in the terminal
     * @param message The message to be printed
     */
    public void acceptPrint(String message){
        reader.printAbove(GREEN+message+RESET);
    }

    /**
     * This is the print method, it prints a normal message above the prompt.
     * @param message The message to be printed
     */
    public void print(String message){
        reader.printAbove(message);
    }

    /**
     * This is the exceptionhandler that prints the exception above the prompt
     * @param e The exception to be printed, it has to be of the Exception class or implement this class
     */
    public void printException(Exception e){
        reader.printAbove(e.toString());
        for(StackTraceElement element : e.getStackTrace()){
            reader.printAbove("\t"+element.toString());
        }
    }

    /**
     * This is the confirm method, it asks the user for a "y" or "yes", and returns true if this is passed to the command line, and false if anything else is passed
     * @param prompt The string explaining what the user is confirming
     * @return a boolean showing whether it has been confirmed or denied
     */
    public boolean confirm(String prompt){
        String result = reader.readLine(prompt);
        return result.equalsIgnoreCase("y") || result.equalsIgnoreCase("yes");
    }
    
}

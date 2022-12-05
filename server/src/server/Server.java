package server;
/*
 * This software has been documented using JavaDoc, all classes and methods are documented
 */

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * This is the main class of the server software, it handles initialization of concurrent processes and data collection
 * @param main This is the main method of the server, it runs the server.
 * @param run This is the run method of the server object, it initializes all objects that are used in the software
 * @param getCli This getter returns the command line interface such that other classes can print through it
 * @param getDatabase This getter returns the initialized database object
 * @param interrupt This method interrupts the main thread such that the server can shut down nicely
 * @param getCollectLock This method makes the lock for the sensor map in the SensorManager class visible while the SensorManager is an initialized object
 * @param removeSensor This method makes the removeSensor method in the SensorManager visible to all other classes while the SensorManager is an initialized object
 * @param clearSensors This method makes the clear method in the SensorManager visible to all other classes while the SensorManager is an initialized object
 */
public class Server {
    static private SensorManager sensorManager;
    static private RequestHandler requestHandler;
    static private Log log;
    static private Database db;
    static private CommandLineInterface commandLineInterface;
    static private Server server;
    static private Thread mainThread = Thread.currentThread();

    /**
     * This is the constructor for the Server object, it initializes the objects used in the server
     * @throws IOException
     */
    private Server() throws IOException{
        sensorManager = new SensorManager();
        log = new Log();
        requestHandler = new RequestHandler();
        commandLineInterface = new CommandLineInterface();
        db = new Database();
        
    }

    /**
     * This is the main method of the server software, it is required to run a java program. it runs the server
     * @param args The arguments passed to the server, it is required to run a java program.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
        server = new Server();
        //db.clear();
        //log.clear();
        server.run();
    }
    /**
     * This is the run method, it runs all threads of the server and continually requests data from the sensors in a loop, calling the sensorManager objects collect method
     * @throws IOException
     */
    public void run() throws IOException{
        Thread sensorManagerThread = new Thread(sensorManager);
        Thread requestHandlerThread = new Thread(requestHandler);
        Thread commandLineInterfaceThread = new Thread(commandLineInterface);
        sensorManagerThread.setDaemon(true);
        requestHandlerThread.setDaemon(true);
        commandLineInterfaceThread.setDaemon(true);
        sensorManagerThread.start();
        requestHandlerThread.start();
        commandLineInterfaceThread.start();
        while(commandLineInterfaceThread.isAlive()){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                return;
            }
            if(sensorManager.getSensorCount() > 0){
                Map<String, String> data = sensorManager.collect();
                commandLineInterface.acceptPrint("Collected a set of data");
                db.save(data);
            }
        }
    }

    /**
     * This is the getter for the commandLineInterface attribute
     * @return the CommandLineInterface class initialized as an object
     */
    static public CommandLineInterface getCli(){
        return commandLineInterface;
    }

    /**
     * This is the getter for the log attribute
     * @return the Log class initialized as an object
     */
    static public Log getLog(){
        return log;
    }

    /**
     * This is the getter for the db attribute
     * @return the Database class initializeed as an object
     */
    static public Database getDatabase(){
        return db;
    }

    /**
     * This method interrupts the main thread such that the server can shut down nicely
     */
    static public void interrupt(){
        mainThread.interrupt();
    }

    /**
     * This method makes the lock from the sensorManager visible to all other classes, as the collectLock is a non-static object
     * @return a lock for the sensors attribute in the SensorManager class
     */
    public static Lock getCollectLock(){
        return sensorManager.getCollectLock();
    }

    /**
     * This method makes the removeSensor method from the sensorManager object visible to all other classes, as this method is a non-static method
     * @param id The id of the sensor to be removed
     */
    public static void removeSensor(String id) {
        sensorManager.removeSensor(id);

    }

    /**
     * This method makes the clear method from the sensorManager object visisble to all other classes as this method is a non-static method
     */
    public static void clearSensors() {
        sensorManager.clear();
    }
}
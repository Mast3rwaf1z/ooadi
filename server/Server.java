package server;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;


public class Server {
    static private SensorManager sensorManager;
    static private RequestHandler requestHandler;
    static private Log log;
    static private Database db;
    static private CommandLineInterface commandLineInterface;
    static private Server server;
    static private Thread mainThread = Thread.currentThread();

    private Server() throws IOException{
        sensorManager = new SensorManager();
        log = new Log();
        requestHandler = new RequestHandler();
        commandLineInterface = new CommandLineInterface();
        db = new Database();
        
    }

    public static void main(String[] args) throws IOException {
        server = new Server();
        //db.clear();
        log.clear();
        server.run();
    }
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

    static public CommandLineInterface getCli(){
        return commandLineInterface;
    }
    static public Log getLog(){
        return log;
    }

    static public Database getDatabase(){
        return db;
    }

    static public void interrupt(){
        mainThread.interrupt();
    }

    public static Lock getCollectLock(){
        return sensorManager.getCollectLock();
    }

    public static void removeSensor(String id) {
        sensorManager.removeSensor(id);

    }

    public static void clearSensors() {
        sensorManager.clear();
    }
}
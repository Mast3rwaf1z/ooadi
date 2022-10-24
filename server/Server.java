package server;

import java.io.IOException;
import java.util.Map;

public class Server {
    private SensorManager sensorManager;
    private RequestHandler requestHandler;
    static private Log log;
    static private Database db;
    static private CommandLineInterface commandLineInterface;
    static private Server server;

    private Server() throws IOException{
        sensorManager = new SensorManager();
        log = new Log();
        requestHandler = new RequestHandler();
        commandLineInterface = new CommandLineInterface();
        db = new Database();
        
    }

    public static void main(String[] args) throws IOException {
        server = new Server();
        db.clear();
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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                commandLineInterface.printException(e);
            }
            Map<String, String> data = sensorManager.collect();
            commandLineInterface.print("Collected a set of data");
            db.save(data);
        }
    }

    static public CommandLineInterface getCli(){
        return commandLineInterface;
    }
}
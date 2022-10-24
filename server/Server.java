package server;

import java.io.IOException;

public class Server {
    private SensorManager sensorManager;
    private Log log;
    private RequestHandler requestHandler;
    static private CommandLineInterface commandLineInterface;
    static Server server;

    private Server() throws IOException{
        sensorManager = new SensorManager();
        log = new Log();
        requestHandler = new RequestHandler();
        commandLineInterface = new CommandLineInterface();
        
    }

    public static void main(String[] args) throws IOException {
        server = new Server();
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
            sensorManager.collect();
            commandLineInterface.print("Collected a set of data");
        }
    }

    static public CommandLineInterface getCli(){
        return commandLineInterface;
    }
}
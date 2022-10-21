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
    public void run(){
        Thread sensorManagerThread = new Thread(sensorManager);
        Thread requestHandlerThread = new Thread(requestHandler);
        sensorManagerThread.setDaemon(true);
        requestHandlerThread.setDaemon(true);
        sensorManagerThread.start();
        requestHandlerThread.start();
        commandLineInterface.run();
    }

    static public CommandLineInterface getCli(){
        return commandLineInterface;
    }
}
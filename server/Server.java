package server;

public class Server {
    SensorManager sensorManager = new SensorManager();
    Log Log = new Log();
    RequestHandler requestHandler = new RequestHandler();
    CommandLineInterface commandLineInterface = new CommandLineInterface();
    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
    public void run(){
        Thread sensorManagerThread = new Thread(sensorManager);
        Thread requestHandlerThread = new Thread(requestHandler);
        Thread commandLineInterfaceThread = new Thread(commandLineInterface);
        sensorManagerThread.start();
        requestHandlerThread.start();
        commandLineInterfaceThread.start();
    }
}
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import server.events.InvalidRequestEvent;

public class Client implements Runnable{
    private String address;
    private int id;
    private Thread thread;
    private Socket socket;

    public Client(Socket socket, int id){
        this.socket = socket;
        this.id = id;
        this.address = socket.getInetAddress().getHostAddress();
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            for(String request = ""; request != null && !request.equals("end"); request = reader.readLine()){
                String[] args = request.split(" ");
                switch(args[0]){
                    case "": 
                        break;
                    default:
                        Server.getCli().errorPrint("Error: client #"+id+" sent an invalid request: ["+request+"]");
                        Server.getLog().add(new InvalidRequestEvent(request, id, address));
                        break;
                }
            }
            Server.getCli().debugPrint("Client disconnected normally");
        } catch (IOException e) {
            Server.getCli().printException(e);
        }
            
    }

    public void transmit(byte[] data){

    }
    
}

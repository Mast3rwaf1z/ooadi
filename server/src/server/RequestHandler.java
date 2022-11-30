package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import server.events.ClientConnectEvent;
import server.events.ClientLoginEvent;
import server.events.ClientLoginFailedEvent;

public class RequestHandler implements Runnable{
    private Map<Integer, Client> clients = new HashMap<>();
    private ServerSocket socket;

    public RequestHandler(){
        try {
            socket = new ServerSocket(8000);
        } catch (IOException e) {
            Server.getCli().errorPrint("Error, could not bind requestHandler socket");
            System.exit(1);
        }
    }

    @Override
    public void run() {
        int id = 0;
        try {
            for(Socket client = socket.accept();; client = socket.accept()){
                id++;
                Server.getLog().add(new ClientConnectEvent(client.getInetAddress().getHostAddress(), id));
                String line = new BufferedReader(new InputStreamReader(client.getInputStream())).readLine();
                if(line == null){
                    Server.getCli().errorPrint("Client disconnected before sending a login request");
                    continue;
                }
                String[] request = line.split(" ");
                
                if(!request[0].equals("login") && request.length != 3){
                    Server.getCli().errorPrint("Client sent an invalid login request");
                    Server.getLog().add(new ClientLoginFailedEvent(client.getInetAddress().getHostAddress(), id));
                    client.close();
                    continue;
                }

                if(!login(request[1], request[2])){
                    Server.getCli().errorPrint("Client failed to log in");
                    Server.getLog().add(new ClientLoginFailedEvent(client.getInetAddress().getHostAddress(), id));
                    try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))){
                        writer.write("failed");
                        writer.flush();
                    }
                    client.close();
                    continue;
                }
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                writer.write("success");
                writer.flush();

                Server.getCli().acceptPrint("Client successfully connected!");
                Server.getLog().add(new ClientLoginEvent(client.getInetAddress().getHostAddress(), id));
                clients.put(id, new Client(client, id));
                
            }
        } catch (IOException e) {
            Server.getCli().errorPrint("Failed to create client socket");
        }
        
        
    }

    public boolean login(String username, String password){
        if(Server.getDatabase().getLogin(username) == null){
            return false;
        }
        return Server.getDatabase().getLogin(username).equals(password);
    }
    
}

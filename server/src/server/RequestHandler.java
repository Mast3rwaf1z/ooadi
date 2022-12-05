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

/**
 * This is the RequestHandler, it handles clients connecting and verifies them
 * @param run This is the supertype run method of the Runnable class. This is run when passed to a threading object
 * @param login verifies the credentials received from a client, returns a boolean based on the validity of these credentials
 */
public class RequestHandler implements Runnable{
    private Map<Integer, Client> clients = new HashMap<>();
    private ServerSocket socket;

    /**
     * This is the constructor, it starts a server socket on port 8000
     */
    public RequestHandler(){
        try {
            socket = new ServerSocket(8000);
        } catch (IOException e) {
            Server.getCli().errorPrint("Error, could not bind requestHandler socket");
            System.exit(1);
        }
    }

    /**
     * This is the supertype run method of the Runnable class, it listens for new clients and verifies their login credentials, it handles early disconnects, invalid login requests and invalid credentials
     */
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

    /**
     * This is the login method, it verifies the login credentials of a client logging in
     * @param username The username received
     * @param password The password received
     * @return A boolean symbolizing whether the login was correct or not
     */
    public boolean login(String username, String password){
        if(Server.getDatabase().getLogin(username) == null){
            return false;
        }
        return Server.getDatabase().getLogin(username).equals(password);
    }
    
}

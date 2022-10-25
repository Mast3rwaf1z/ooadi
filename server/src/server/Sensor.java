package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Sensor {
    private InetAddress address;
    private Socket socket;

    public Sensor(Socket socket){
        this.socket = socket;
        address = socket.getInetAddress();
    }
    
    public void transmit(String data) throws IOException{
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(data+"\n");
        writer.flush();
    }
    public String receive() throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return reader.readLine();
    }

    public String getAddress() {
        return address.getHostAddress();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            Server.getCli().printException(e);
        }
    }
}

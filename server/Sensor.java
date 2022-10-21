package server;

import java.net.Socket;

public class Sensor {
    private String address;
    private Socket socket;

    public Sensor(Socket socket){
        this.socket = socket;
    }
    
    public void transmit(byte[] data){

    }
    public byte[] receive(){
        return null;
    }
}

package server;

public class Client implements Runnable{
    private String address = "localhost";

    @Override
    public void run() {
        address.toString(); //TODO: remove later, just removing warnings
    }

    public void transmit(byte[] data){

    }
    
}

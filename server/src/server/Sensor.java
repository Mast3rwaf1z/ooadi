package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This is the Sensor class, it abstracts important methods for handling the sensors
 * @param transmit This method transmits a string to the sensor
 * @param receive This method receives a string from the sensor
 * @param getAddress This is the getter for the address attribute
 * @param close This method closes the socket if the sensor has been removed from the network
 */
public class Sensor {
    private InetAddress address;
    private Socket socket;

    /**
     * This is the constructor, it takes one parameter
     * @param socket This is the socket to the sensor, data sent to and from the sensor is through this socket
     */
    public Sensor(Socket socket){
        this.socket = socket;
        address = socket.getInetAddress();
    }
    
    /**
     * This is the transmit method, it abstracts transmitting data to the sensors such that it is consistent across the server software
     * @param data The data to be transmitted to the sensor
     * @throws IOException
     */
    public void transmit(String data) throws IOException{
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(data+"\n");
        writer.flush();
    }

    /**
     * This is the receive method, it abstracts receiving data from a sensor such that it is consistent across the server software
     * @return The data received from the sensor
     * @throws IOException
     */
    public String receive() throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return reader.readLine();
    }

    /**
     * This is the getter for the address attribute, it is an internet address
     * @return a string containing the internet address of the sensor
     */
    public String getAddress() {
        return address.getHostAddress();
    }

    /**
     * Closes the connection to a sensor
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            Server.getCli().printException(e);
        }
    }
}

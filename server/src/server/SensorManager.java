package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import server.events.SensorConnectEvent;
import server.events.SensorConnectionRefusedEvent;
import server.events.SensorDisconnectEvent;

/**
 * This is the SensorManager class, it implements the class Runnable
 * @param run The supertype method run of the class Runnable overridden, this method accepts and verifies sensors before adding them to a map of sensors so be used later
 * @param collect The main loop of the program, it periodically requests data from sensors and receives this data afterwards
 * @param getSensorCount Gets the amount of sensors that are registered in at the moment of the call
 * @param getCollectLock Returns the lock such that the map of sensors isn't being written to concurrently
 * @param removeSensor Removes a sensor from tthe map of sensors
 * @param clear removes all sensors from the map of sensors
 * @param login Checks if the master password sent by the sensor is correct
 */
public class SensorManager implements Runnable {
    private Map<String, Sensor> sensors = new HashMap<>();
    private ServerSocket socket;
    private Lock collectLock;

    /**
     * This is the constructor, it creates a new server socket on port 8888 and initializes the collect lock
     * @throws IOException
     */
    public SensorManager() throws IOException{
        this.socket = new ServerSocket(8888);
        collectLock = new ReentrantLock();
    }

    /**
     * This is the supertype method run of the class Runnable. It handles accepting sensors and verifying these sensors
     */
    @Override
    public void run() {
        Server.getCli().debugPrint("accepting sensors");
        while(true){
            try {
                Socket sensorSocket = socket.accept();
                String[] loginRequest = new BufferedReader(new InputStreamReader(sensorSocket.getInputStream())).readLine().split(" ");
                if(loginRequest.length != 2){
                    Server.getCli().errorPrint("Error, not enough arguments!");
                    Server.getLog().add(new SensorConnectionRefusedEvent(null, sensorSocket.getInetAddress().getHostAddress()));
                    sensorSocket.close();
                    continue;
                }
                if(!login(loginRequest[0])){
                    Server.getCli().errorPrint("Error, sensor #"+loginRequest[1]+" wrong master password!");
                    Server.getLog().add(new SensorConnectionRefusedEvent(loginRequest[1], sensorSocket.getInetAddress().getHostAddress()));
                    sensorSocket.close();
                    continue;
                }
                if(sensors.containsKey(loginRequest[1])){
                    Server.getCli().errorPrint("Error, sensor #"+loginRequest[1]+" id already exists");
                    Server.getLog().add(new SensorConnectionRefusedEvent(loginRequest[1], sensorSocket.getInetAddress().getHostAddress()));
                    sensorSocket.close();
                    continue;
                }
                Server.getCli().acceptPrint("Accepted sensor with id: "+loginRequest[1]);
                Server.getLog().add(new SensorConnectEvent(loginRequest[1], sensorSocket.getInetAddress().getHostAddress()));
                Server.getDatabase().addEntry(loginRequest[1]);
                collectLock.lock();
                sensors.put(loginRequest[1], new Sensor(sensorSocket));
                collectLock.unlock();
            } catch (IOException e) {
                Server.getCli().printException(e);
            }
            
        }
    }
    
    /**
     * This is the collect method, this is run as the main loop of the server software.
     * @return it returns a map of data where the keys are ids of sensors and the values are the data collected from the sensor
     * @throws IOException
     */
    public Map<String, String> collect() throws IOException{
        collectLock.lock();
        Server.getCli().debugPrint("Requesting data... " + "Sensors available: "+getSensorCount());
        
        Iterator<String> iterator = sensors.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            Sensor sensor = sensors.get(key);
            try{
                sensor.transmit("request");
            }
            catch(SocketException e){
                Server.getCli().errorPrint("Sensor identified by id: "+key+" has disconnected.");
                iterator.remove();
                Server.getLog().add(new SensorDisconnectEvent(key, sensor.getAddress()));
            }
        }
        Map<String, String> result = new HashMap<>();
        Server.getCli().debugPrint("Collecting data");
        iterator = sensors.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            Sensor sensor = sensors.get(key);
            String data = "";
            try{
                data = sensor.receive();
            }
            catch(SocketException e){
                Server.getCli().errorPrint("Sensor identified by id: "+key+" has disconnected.");
                iterator.remove();
                Server.getLog().add(new SensorDisconnectEvent(key, sensor.getAddress()));
            }
            result.put(key, data);
        }   
        collectLock.unlock();
        return result;
    }

    /**
     * This method gets the amount of sensors currently registered
     * @return The amount of sensors registered
     */
    public int getSensorCount() {
        return sensors.size();
    }

    /**
     * This getter gets the lock such that the map of sensors isn't written to concurrently
     * @return the collectLock attribute
     */
    public Lock getCollectLock(){
        return collectLock;
    }

    /**
     * This method removes a sensor from the map of sensors, it uses the collectlock to prevent concurrent writes to the sensor map
     * @param id The id of the sensor to be removed
     */
    public void removeSensor(String id) {
        collectLock.lock();
        if(sensors.containsKey(id)){
            sensors.get(id).close();
            sensors.remove(id);
        }
        collectLock.unlock();
    }
    
    /**
     * This method clears the map of sensors
     */
    public void clear(){
        collectLock.lock();
        for(Sensor sensor : sensors.values()){
            sensor.close();
        }
        sensors = new HashMap<>();
        collectLock.unlock();
    }

    /**
     * This method logs the sensor in by checking whether the master password received from tthe sensor is correct
     * @param password The master password received from a sensor
     * @return a boolean showing whether it is correct or not
     */
    private boolean login(String password){
        return Server.getDatabase().getMasterPassword().equals(password);
    }
    
}

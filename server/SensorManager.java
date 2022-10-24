package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import server.events.SensorConnectEvent;
import server.events.SensorDisconnectEvent;

public class SensorManager implements Runnable {
    private Map<String, Sensor> sensors = new HashMap<>();
    private ServerSocket socket;
    private Lock collectLock;

    public SensorManager() throws IOException{
        this.socket = new ServerSocket(8888);
        collectLock = new ReentrantLock();
    }

    @Override
    public void run() {
        Server.getCli().debugPrint("accepting sensors");
        while(true){
            try {
                Socket sensor = socket.accept();
                Server.getCli().debugPrint("accepted sensor, validating id...");
                List<String> registeredSensors = Server.getDatabase().getIds();
                String id = new BufferedReader(new InputStreamReader(sensor.getInputStream())).readLine();
                Server.getCli().debugPrint("The received id is: " + id);

                if(registeredSensors.contains(id)){
                    Server.getCli().acceptPrint("The id is valid, adding sensor to map of sensors...");
                    sensors.put(id, new Sensor(sensor));
                    Server.getLog().add(new SensorConnectEvent(id, sensor.getInetAddress().getHostAddress()));
                }
                else{
                    Server.getCli().errorPrint("The id is invalid, cutting off the connection!");
                    Server.getLog().add(new SensorDisconnectEvent(id, socket.getInetAddress().getHostAddress()));
                    sensor.close();
                }
            } catch (IOException e) {
                Server.getCli().printException(e);
                
            }
        }
    }
    
    public Map<String, String> collect() throws IOException{
        collectLock.lock();
        Server.getCli().debugPrint("Requesting data...");
        
        for(String key : sensors.keySet()){
            Sensor sensor = sensors.get(key);
            try{
                sensor.transmit("request");
            }
            catch(SocketException e){
                Server.getCli().errorPrint("Sensor identified by id: "+key+" has disconnected.");
                sensors.remove(key);
                Server.getLog().add(new SensorDisconnectEvent(key, sensor.getAddress()));
            }
        }
        Map<String, String> result = new HashMap<>();
        Server.getCli().debugPrint("Collecting data");
        for(String key : sensors.keySet()){
            Sensor sensor = sensors.get(key);
            String data = "";
            try{
                data = sensor.receive();
            }
            catch(SocketException e){
                Server.getCli().errorPrint("Sensor identified by id: "+key+" has disconnected.");
                sensors.remove(key);
                Server.getLog().add(new SensorDisconnectEvent(key, sensor.getAddress()));
            }
            result.put(key, data);
        }   
        collectLock.unlock();
        return result;
    }

    public int getSensorCount() {
        return sensors.size();
    }

    public Lock getCollectLock(){
        return collectLock;
    }

    public void removeSensor(String id) {
        collectLock.lock();
        sensors.remove(id);
        collectLock.unlock();
    }
    
}

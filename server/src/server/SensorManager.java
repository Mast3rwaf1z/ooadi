package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import server.events.SensorConnectEvent;
import server.events.SensorConnectionRefusedEvent;
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
                sensors.put(loginRequest[1], new Sensor(sensorSocket));
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
        if(sensors.containsKey(id)){
            sensors.get(id).close();
            sensors.remove(id);
        }
        collectLock.unlock();
    }
     
    public void clear(){
        for(Sensor sensor : sensors.values()){
            sensor.close();
        }
        sensors = new HashMap<>();
    }

    private boolean login(String password){
        return Server.getDatabase().getMasterPassword().equals(password);
    }
    
}

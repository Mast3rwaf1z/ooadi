package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorManager implements Runnable {
    private Database db;
    private Map<String, Sensor> sensors = new HashMap<>();
    private ServerSocket socket;

    public SensorManager() throws IOException{
        db = new Database();
        this.socket = new ServerSocket(8888);
    }

    @Override
    public void run() {
        Server.getCli().print("accepting sensors");
        while(true){
            try {
                Socket sensor = socket.accept();
                Server.getCli().print("accepted sensor, validating id...");
                List<String> registeredSensors = db.getIds();
                String id = new BufferedReader(new InputStreamReader(sensor.getInputStream())).readLine();
                Server.getCli().print("The received id is: " + id);

                if(registeredSensors.contains(id)){ //TODO: add an actual condition here later
                    Server.getCli().print("The id is valid, adding sensor to map of sensors...");
                    sensors.put(String.valueOf(sensors.size()+1), new Sensor(sensor));
                }
                else{
                    Server.getCli().print("The id is invalid, cutting off the connection!");
                    sensor.close();
                }
            } catch (IOException e) {
                Server.getCli().printException(e);
                
            }
        }
    }
    
    public Map<String, String> collect() throws IOException{
        Server.getCli().print("Requesting data...");
        for(Sensor sensor : sensors.values()){
            sensor.transmit("request");
        }
        Map<String, String> result = new HashMap<>();
        Server.getCli().print("Collecting data");
        for(String key : sensors.keySet()){
            Sensor sensor = sensors.get(key);
            String data = sensor.receive();
            result.put(key, data);
        }   
        return result;
    }
    
}

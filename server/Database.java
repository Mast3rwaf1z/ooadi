package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;

import server.events.DatabaseClearEvent;

public class Database {
    private JSONObject json;

    public Database(){
        loadDatabase();
    }

    private void loadDatabase(){
        try {
            json = new JSONObject(new JSONTokener(new FileInputStream(new File("database.json"))));
        } catch (IOException e) {
            System.out.println("Database file was not found, please run `make init`");
            System.exit(1);
        }

    }

    public List<String> getIds(){
        List<String> result = new ArrayList<>();
        JSONObject sensors = json.getJSONObject("sensors");
        for(String key : sensors.keySet()){
            result.add(key);
        }
        return result;
    }

    public void save(Map<String, String> data) throws IOException{
        JSONObject sensors;
        sensors = json.getJSONObject("sensors");
        for(String key : data.keySet()){
            int sequence_number = 0;
            for(String inner_key : sensors.getJSONObject(key).keySet()){
                if(Integer.parseInt(inner_key) > sequence_number){
                    sequence_number = Integer.parseInt(inner_key);
                }
            }
            sensors.getJSONObject(key).put(String.valueOf(sequence_number+1), data.get(key));
        }
        json.put("sensors", sensors);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File("database.json")))){
            writer.write(json.toString(4));
        }
    }


    public void clear(){
        Server.getLog().add(new DatabaseClearEvent());
        Map<String, JSONObject> sensors = new HashMap<>();
        json.put("sensors", sensors);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File("database.json")))){
            writer.write(json.toString(4));
        }
        catch(IOException e){
            Server.getCli().printException(e);
        }

    }

    public void addEntry(String id) {
        JSONObject sensors = json.getJSONObject("sensors");
        sensors.put(id, new JSONObject());
        json.put("sensors", sensors);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File("database.json")))){
            writer.write(json.toString(4));
        }
        catch(IOException e){
            Server.getCli().printException(e);
        }
    }

    public void removeEntry(String id) {
        JSONObject sensors = json.getJSONObject("sensors");
        sensors.remove(id);
        json.put("sensors", sensors);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File("database.json")))){
            writer.write(json.toString(4));
        }
        catch(IOException e){
            Server.getCli().printException(e);
        }
    }
    public String getLogin(String username){
        if(!json.getJSONObject("users").has(username)){
            return null;
        }
        return json.getJSONObject("users").getString(username);
    }
}
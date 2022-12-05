package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;

import server.events.DatabaseClearEvent;
import server.events.SensorAddEvent;

/**
 * This is the database class, it is responsible for abstracting the database from the rest of the system, in theory this could be adapted to SQL or some other database form, and allow the rest of the server to function normally
 * @param loadDatabase loads the database as a json object
 * @param getIds returns the registered sensor ids
 * @param save saves a collection of data with timestamps to the database
 * @param clear clears the database of all entries
 * @param addEntry adds an entry to the database
 * @param removeEntry removes an entry from the database
 * @param getLogin gets the password of a registered user
 * @param timestamp gets the current time and returns it as a timestamp
 * @param getMasterPassword gets the master password from the database
 * @param addUser adds a new user to the database
 * @param removeUser removes a user from the database
 * @param getSensorData returns some data from a sensor
 */
public class Database {
    private JSONObject json;
    private String path = "server/files/database.json";

    /**
     * This is the constructor, it calls the loadDatabase method, which loads the database into a json object
     */
    public Database(){
        loadDatabase();
    }

    /**
     * This is the loadDatabase method, it reads the database file and initializes it into a json object
     */
    private void loadDatabase(){
        try {
            json = new JSONObject(new JSONTokener(new FileInputStream(new File(path))));
        } catch (IOException e) {
            System.out.println("Database file was not found, please run `make init`");
            System.exit(1);
        }

    }

    /**
     * This is the getIds method, it is responsible for returning all ids of sensors in the database
     * @return a list of strings containing the ids of all sensors
     */
    public List<String> getIds(){
        List<String> result = new ArrayList<>();
        JSONObject sensors = json.getJSONObject("sensors");
        for(String key : sensors.keySet()){
            result.add(key);
        }
        return result;
    }

    /**
     * Saves a map of data to the database
     * @param data This is the map of data, the keys of this map is the ids of sensors, while the values are the data received.
     * @throws IOException
     */
    public void save(Map<String, String> data) throws IOException{
        JSONObject sensors;
        sensors = json.getJSONObject("sensors");
        for(String key : data.keySet()){
            sensors.getJSONObject(key).put(timestamp(), data.get(key));
        }
        json.put("sensors", sensors);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)))){
            writer.write(json.toString(4));
        }
    }

    /**
     * This is the clear method, it clears the database of all entries
     */
    public void clear(){
        Server.getLog().add(new DatabaseClearEvent());
        Map<String, JSONObject> sensors = new HashMap<>();
        json.put("sensors", sensors);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)))){
            writer.write(json.toString(4));
        }
        catch(IOException e){
            Server.getCli().printException(e);
        }

    }

    /**
     * This is the addEntry method, it adds a sensor as an entry in the database.
     * @param id This is the id of the sensor to be added to the database
     */
    public void addEntry(String id) {
        if(json.getJSONObject("sensors").has(id)){
            return;
        }
        JSONObject sensors = json.getJSONObject("sensors");
        sensors.put(id, new JSONObject());
        Server.getLog().add(new SensorAddEvent(id));
        json.put("sensors", sensors);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)))){
            writer.write(json.toString(4));
        }
        catch(IOException e){
            Server.getCli().printException(e);
        }
    }

    /**
     * This is the removeEntry method, it removes a sensor as an entry from the database.
     * @param id This is the id of the sensor to be removed from the database
     */
    public void removeEntry(String id) {
        JSONObject sensors = json.getJSONObject("sensors");
        sensors.remove(id);
        json.put("sensors", sensors);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)))){
            writer.write(json.toString(4));
        }
        catch(IOException e){
            Server.getCli().printException(e);
        }
    }

    /**
     * This is the method used for when a client is attempting to log in to the server
     * @param username This is the username given to the server
     * @return returns null if the username doesn't exist, or the password if it exists
     */
    public String getLogin(String username){
        if(!json.getJSONObject("users").has(username)){
            return null;
        }
        return json.getJSONObject("users").getString(username);
    }

    /**
     * This is the timestamp method, it returns a timestamp formatted like this: 05/12/2022 - 10:57:00
     * @return a timestamp formatted like this: 05/12/2022 - 10:57:00
     */
    private String timestamp(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss"));

    }

    /**
     * This is the master password getter, it gets the master password from the database
     * @return a string containing only the master password
     */
    public String getMasterPassword() {
        return json.getString("password");
    }
        
    /**
     * Adds a new user as an entry in the database
     * @param username The username of the user
     * @param password The password of the user
     */
    public void addUser(String username, String password) {
        JSONObject users = json.getJSONObject("users");
        users.put(username, password);
        json.put("users", users);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)))){
            writer.write(json.toString(4));
        }
        catch(IOException e){
            Server.getCli().printException(e);
        }
    }

    /**
     * Removes a user as an entry from the database
     * @param username The username of the user
     */
    public void removeUser(String username){
        JSONObject users = json.getJSONObject("users");
        users.remove(username);
        json.put("users", users);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)))){
            writer.write(json.toString(4));
        }
        catch(IOException e){
            Server.getCli().printException(e);
        }
    }

    /**
     * gets all data of a sensor from the database.
     * @param id The id of the sensor being requested
     * @return a map of data, the keys are timestamps while the values is the data for that timestamp
     */
    public Map<String, String> getSensorData(String id) {
        Map<String, String> result = new HashMap<>();
        JSONObject sensor = json.getJSONObject("sensors").getJSONObject(id);
        for(String key : sensor.keySet()){
            result.put(key, sensor.getString(key));
        }
        return result;
    }
}
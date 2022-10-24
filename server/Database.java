package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Database {
    private JSONObject json;

    public Database() throws IOException{
        json = new JSONObject(new JSONTokener(new FileInputStream(new File("database.json"))));
    }

    public List<String> getIds() throws IOException{
        List<String> result = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(new File("database.json")))){
            JSONObject sensors = (JSONObject)json.get("sensors");
            for(String key : sensors.keySet()){
                result.add(key);
            }
        }
        return result;
    }

    public void save(Map<String, String> data) throws IOException{
        JSONObject sensors;
        try(BufferedReader reader = new BufferedReader(new FileReader(new File("database.json")))){
            sensors = (JSONObject)json.get("sensors");
            for(String key : data.keySet()){
                int sequence_number = 0;
                for(Object inner_key : ((JSONObject)sensors.get(key)).keySet()){
                    if(Integer.parseInt((String)inner_key) > sequence_number){
                        sequence_number = Integer.parseInt((String)inner_key);
                    }
                }
                ((JSONObject)sensors.get(key)).put(String.valueOf(sequence_number+1), data.get(key));
            }
        }
        json.put("sensors", (JSONObject)sensors);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File("database.json")))){
            writer.write(json.toString(4));
        }
    }


    public void clear() throws IOException {
        Map<String, JSONObject> sensors = new HashMap<>();
        List<String> ids = getIds();
        for(String id : ids){
            sensors.put(id, new JSONObject());
        }
        json.put("sensors", sensors);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File("database.json")))){
            writer.write(json.toString(4));
        }

    }
}

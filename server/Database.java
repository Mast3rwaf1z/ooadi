package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Database {

    @SuppressWarnings("unchecked")
    public List<String> getIds() throws IOException, ParseException{
        JSONParser parser = new JSONParser();
        List<String> result = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(new File("database.json")))){
            Map<String, ?> sensors = (Map<String, ?>)((Map<String, Map<String, ?>>) parser.parse(reader)).get("sensors");
            for(String key : sensors.keySet()){
                result.add(key);
            }
        }
        return result;
    }
}

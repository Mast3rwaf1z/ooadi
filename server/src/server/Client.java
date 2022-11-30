package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.events.ClientDataRequestEvent;
import server.events.ClientDisconnectEvent;
import server.events.InvalidRequestEvent;

public class Client implements Runnable{
    private String address;
    private int id;
    private Thread thread;
    private Socket socket;

    public Client(Socket socket, int id){
        this.socket = socket;
        this.id = id;
        this.address = socket.getInetAddress().getHostAddress();
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            for(String request = ""; request != null && !request.equals("end"); request = reader.readLine()){
                try{
                    String[] args = request.split(" ");
                    String response;
                    switch(args[0]){
                        case "": 
                            continue;
                        case "getdata":
                        //expects [getdata <sensorid> <amount>], it starts at the newest measurement and goes back n times
                            response = args.length == 3 ? getData(args[1], Integer.parseInt(args[2])) : "Error, invalid arguments, expected: [getdata <sensorid> <amount>]";
                            break;
                        case "getrange":
                            //returns how many measurements that are available per sensor - getrange <id>
                            response = args.length == 2 ? getRange(args[1]) : "Error!";
                            break;
                        case "getids":
                            //returns all ids
                            response = getIds();
                            break;
                        default:
                            Server.getCli().errorPrint("Error: client #"+id+" sent an invalid request: ["+request+"]");
                            Server.getLog().add(new InvalidRequestEvent(request, id, address));
                            response = "Error: client #"+id+" sent an invalid request: ["+request+"]";
                            break;
                    }
                    transmit(response);
                }
                catch(Exception e){
                    Server.getCli().printException(e);
                }
            }   
            Server.getCli().debugPrint("Client disconnected normally");
            Server.getLog().add(new ClientDisconnectEvent(socket.getInetAddress().getHostAddress(), id));
        } catch (IOException e) {
            Server.getCli().printException(e);
        }
            
    }

    public void transmit(String data){
        try{
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(data);
            writer.flush();
        }
        catch(IOException e){
            Server.getCli().printException(e);
        }
    }

    private String getData(String id, int amount){
        Map<String, String> data = Server.getDatabase().getSensorData(id);
        Map<Long, String> data_formatted = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        for(String timestamp : data.keySet()){
            try {
                data_formatted.put(format.parse(timestamp).getTime(), data.get(timestamp));
            } catch (ParseException e) {
                Server.getCli().printException(e);
            }
        }
        List<Long> keys = new ArrayList<>(data_formatted.keySet());
        Collections.sort(keys);
        String message = "getdatareply:{\n";
        for(int i = keys.size()-1; i > keys.size()-amount; i--){
            message+="\t\""+format.format(keys.get(i))+"\":\""+data_formatted.get(keys.get(i))+"\",\n";
        }
        message+="\t\""+format.format(keys.get(keys.size()-amount))+"\":\""+data_formatted.get(keys.get(keys.size()-amount))+"\"\n}";
        Server.getLog().add(new ClientDataRequestEvent(id, address, String.valueOf(amount)));
        return message;

    }
    private String getIds(){
        List<String> ids = Server.getDatabase().getIds();
        String message = "getidsreply:";
        for(String id : ids){
            message+=id+" ";
        }
        message = message.strip();
        return message;

    }
    private String getRange(String id){
        int amount = Server.getDatabase().getSensorData(id).size();
        return "getrangereply:"+amount;

    }
    
}

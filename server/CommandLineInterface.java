package server;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

public class CommandLineInterface implements Runnable {
    private LineReader reader = LineReaderBuilder.builder().build();
    private String prompt = "> ";

    @Override
    public void run() {
        for(String line = ""; !line.equals("exit"); line = reader.readLine(prompt)){

        }
    }

    private void addSensor(String addr, int id){

    }

    private void removeSensor(String addr, int id){
        
    }
    
}

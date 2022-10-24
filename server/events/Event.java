package server.events;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface Event {
    public String build();
    default public String builder(String callData){
        return timestamp()+" "+this.getClass().getSimpleName()+"["+callData+"]";
    }

    default public String timestamp(){
        return "["+LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"))+"]";

    }
}

package server.events;

public class SensorConnectionRefusedEvent implements Event{
    private String id;
    private String addr;

    public SensorConnectionRefusedEvent(String id, String addr){
        this.id = id;
        this.addr = addr;
    }

    @Override
    public String build() {
        return builder("id: "+id+" addr: "+addr);
    }
    
}

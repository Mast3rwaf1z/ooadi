package server.events;


public class SensorDisconnectEvent implements Event{
    private String id;
    private String addr;

    public SensorDisconnectEvent(String id, String addr){
        this.id = id;
        this.addr = addr;
    }

    @Override
    public String build() {
        return this.builder("id: "+id+" addr: "+addr);
    }
    
}

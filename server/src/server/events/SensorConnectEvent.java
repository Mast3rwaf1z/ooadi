package server.events;


public class SensorConnectEvent implements Event{
    private String addr;
    private String id;

    public SensorConnectEvent(String id, String addr){
        this.id = id;
        this.addr = addr;
    }

    @Override
    public String build() {
        return this.builder("id: "+id+" addr: "+addr);
    }
    
}

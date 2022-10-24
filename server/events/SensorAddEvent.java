package server.events;


public class SensorAddEvent implements Event {

    private String id;
    private String addr;

    public SensorAddEvent(String id, String addr){
        this.id = id;
        this.addr = addr;
    }

    @Override
    public String build() {
        return this.builder("id: "+id+" addr: "+addr);
    }
    
}

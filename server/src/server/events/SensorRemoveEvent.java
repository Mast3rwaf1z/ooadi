package server.events;

public class SensorRemoveEvent implements Event {

    private String id;

    public SensorRemoveEvent(String id){
        this.id = id;
    }

    @Override
    public String build() {
        return this.builder("id: "+id);
    }
    
}

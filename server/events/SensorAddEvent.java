package server.events;


public class SensorAddEvent implements Event {

    private String id;

    public SensorAddEvent(String id){
        this.id = id;
    }

    @Override
    public String build() {
        return this.builder("id: "+id);
    }
    
}

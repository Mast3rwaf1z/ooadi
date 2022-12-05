package server.events;

/**
 * This class implements the Event class.
 * It is called when a sensor disconnects from the server
 */
public class SensorDisconnectEvent implements Event{
    private String id;
    private String addr;

    /**
     * This is the constructor, it takes two parameters
     * @param id This is the id of the sensor
     * @param addr This is the internet address of the sensor
     */
    public SensorDisconnectEvent(String id, String addr){
        this.id = id;
        this.addr = addr;
    }

    @Override
    public String build() {
        return this.builder("id: "+id+" addr: "+addr);
    }
    
}

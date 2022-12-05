package server.events;

/**
 * This class implements the Event class.
 * It is called when a sensor sends an invalid id or an invalid master password
 */
public class SensorConnectionRefusedEvent implements Event{
    private String id;
    private String addr;

    /**
     * This is the constructor, it has two parameters
     * @param id This is the id of the sensor
     * @param addr This is the internet address of the sensor
     */
    public SensorConnectionRefusedEvent(String id, String addr){
        this.id = id;
        this.addr = addr;
    }

    @Override
    public String build() {
        return builder("id: "+id+" addr: "+addr);
    }
    
}

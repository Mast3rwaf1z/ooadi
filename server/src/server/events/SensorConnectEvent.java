package server.events;

/**
 * This class implements the Event class.
 * This class is called when a sensor connects to the server
 */
public class SensorConnectEvent implements Event{
    private String addr;
    private String id;

    /**
     * This is the constructor, it takes two parameters
     * @param id This is the id of the sensor
     * @param addr This is the internet address of the sensor
     */
    public SensorConnectEvent(String id, String addr){
        this.id = id;
        this.addr = addr;
    }

    @Override
    public String build() {
        return this.builder("id: "+id+" addr: "+addr);
    }
    
}

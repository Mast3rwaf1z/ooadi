package server.events;

/**
 * This class implements the Event class.
 * It is called when a sensor is removed from the database
 */
public class SensorRemoveEvent implements Event {

    private String id;

    /**
     * This is the constructor, it takes two parameters
     * @param id This is the id of the sensor being logged
     */
    public SensorRemoveEvent(String id){
        this.id = id;
    }

    @Override
    public String build() {
        return this.builder("id: "+id);
    }
    
}

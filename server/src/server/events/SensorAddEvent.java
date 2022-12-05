package server.events;

/**
 * This class implements the Event class.
 * It is called when a sensor is added to the database
 */
public class SensorAddEvent implements Event {

    private String id;

    /**
     * This is the constructor, it takes one parameter
     * @param id The id of the sensor being logged
     */
    public SensorAddEvent(String id){
        this.id = id;
    }

    @Override
    public String build() {
        return this.builder("id: "+id);
    }
    
}

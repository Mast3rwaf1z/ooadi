package server.events;

/**
 * This class implements the Event class.
 * This class is called whenever a request from a sensor is invalid, that could be if there is an invalid amount of parameters, wrong value in the parameters or a command doesn't exist
 */
public class InvalidRequestEvent implements Event{

    private String request;
    private int id;
    private String addr;

    /**
     * This is the constructor of the class, it takes 3 parameters
     * @param request Is the actual payload sent to the server
     * @param id The id of the client that sent the request
     * @param addr The internet address of the client
     */
    public InvalidRequestEvent(String request, int id, String addr){
        this.request = request;
        this.id = id;
        this.addr = addr;

    }

    @Override
    public String build() {
        return builder("addr: "+addr+" id: "+id+" request: "+request);
    }
    
}

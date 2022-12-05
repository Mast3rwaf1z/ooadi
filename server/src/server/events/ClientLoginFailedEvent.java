package server.events;

/**
 * This class implements the Event class.
 * This event is logged when a client login request is failed
 */
public class ClientLoginFailedEvent implements Event{

    private String addr;
    private int id;

    /**
     * This is the constructor, it has two parameters
     * @param addr This is the internet address of the client
     * @param id This is the id of the client
     */
    public ClientLoginFailedEvent(String addr, int id){
        this.addr = addr;
        this.id = id;
    }

    @Override
    public String build() {
        return builder("addr: "+addr+" id: "+id);
    }
    
}

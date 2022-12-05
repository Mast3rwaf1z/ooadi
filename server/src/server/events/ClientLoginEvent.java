package server.events;

/**
 * This class implements the Event class.
 * It logs when the client has logged in to the server
 */
public class ClientLoginEvent implements Event {

    private String addr;
    private int id;

    /**
     * This is the constructor, it has two parameters
     * @param addr The internet address of a client
     * @param id The id of a client
     */
    public ClientLoginEvent(String addr, int id){
        this.addr = addr;
        this.id = id;

    }

    @Override
    public String build() {
        return builder("addr: "+addr+" id: "+id);
    }
    
}

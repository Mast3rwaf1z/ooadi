package server.events;

/**
 * This class implements the Event class.
 * It is called when a client disconnects from the server
 */
public class ClientDisconnectEvent implements Event {

    private String addr;
    private int id;

    /**
     * This is the constructor, it has two parameters
     * @param addr This is the internet address of the client
     * @param id This is the id of a client
     */
    public ClientDisconnectEvent(String addr, int id){
        this.addr = addr;
        this.id = id;
    }

    @Override
    public String build() {
        return builder("addr: "+addr+" id: "+id);
    }
    
}

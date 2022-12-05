package server.events;
/**
 * This class implements the Event class.
 * it logs that a client has connected to the server
 */
public class ClientConnectEvent implements Event {

    private String addr;
    private int id;

    /**
     * This constructor saves the internet address of the client and its id
     * @param addr the internet address
     * @param id the id of the client
     */
    public ClientConnectEvent(String addr, int id){
        this.addr = addr;
        this.id = id;
    }

    @Override
    public String build() {
        return builder("addr: "+addr +" id: "+id);
    }
    
}

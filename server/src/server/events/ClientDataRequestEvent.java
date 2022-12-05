package server.events;

/**
 * This class implements the Event class.
 * It is called when the clients requests data from the server
 */
public class ClientDataRequestEvent implements Event {

    private String id;
    private String amount;
    private String addr;

    /**
     * This is the constructor, it logs the arguments the client had sent to the server
     * @param id The id of the sensor being requested data of
     * @param addr The internet address of the sensor
     * @param amount The amount of data, this is an integer in string form
     */
    public ClientDataRequestEvent(String id, String addr, String amount){
        this.id = id;
        this.addr = addr;
        this.amount = amount;
        
    }

    @Override
    public String build() {
        return builder("addr: "+addr + " id: "+id+" amount: "+amount);
    }
    
}

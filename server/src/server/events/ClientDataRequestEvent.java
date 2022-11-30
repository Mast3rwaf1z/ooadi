package server.events;

public class ClientDataRequestEvent implements Event {

    private String id;
    private String amount;
    private String addr;

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

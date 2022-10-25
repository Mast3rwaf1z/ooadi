package server.events;

public class ClientDisconnectEvent implements Event {

    private String addr;
    private int id;

    public ClientDisconnectEvent(String addr, int id){
        this.addr = addr;
        this.id = id;
    }

    @Override
    public String build() {
        return builder("addr: "+addr+" id: "+id);
    }
    
}

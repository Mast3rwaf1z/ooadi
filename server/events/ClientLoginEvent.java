package server.events;

public class ClientLoginEvent implements Event {

    private String addr;
    private int id;

    public ClientLoginEvent(String addr, int id){
        this.addr = addr;
        this.id = id;

    }

    @Override
    public String build() {
        return builder("addr: "+addr+" id: "+id);
    }
    
}

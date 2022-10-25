package server.events;

public class ClientLoginFailedEvent implements Event{

    private String addr;
    private int id;

    public ClientLoginFailedEvent(String addr, int id){
        this.addr = addr;
        this.id = id;
    }

    @Override
    public String build() {
        return builder("addr: "+addr+" id: "+id);
    }
    
}

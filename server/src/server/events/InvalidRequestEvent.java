package server.events;

public class InvalidRequestEvent implements Event{

    private String request;
    private int id;
    private String addr;

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

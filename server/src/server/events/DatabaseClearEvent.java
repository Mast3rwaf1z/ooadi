package server.events;

public class DatabaseClearEvent implements Event{

    @Override
    public String build() {
        return this.builder("");
    }
    
}

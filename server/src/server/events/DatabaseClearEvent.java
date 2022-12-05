package server.events;

/**
 * This class implements the Event class.
 * This class logs the database being cleared by the command line.
 */
public class DatabaseClearEvent implements Event{

    @Override
    public String build() {
        return this.builder("");
    }
    
}

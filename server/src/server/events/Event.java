package server.events;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This interface is the backbone of the logging functionality of the server, it has the following methods:
 * @param build Calls builder with custom arguments, it has to be overridden
 * @param builder A default method that logs timestamp, class name and any data specified in build
 * @param timestamp Is a default method that returns the current time in a desired human readable format
 * 
 */
public interface Event {
    /**
     * This is the build method, it has to be overridden in a class implementing the interface
     * @return the log message in string form, ready to be written to the log
     */
    public String build();
    /**
     * This is the builder method, it is a default method that is called within the build method, it takes calldata 
     * @param callData is the data to be logged, it is expected to be formatted like this: "somethingToBeLogged: itsValue somethingElse: itsValue2"
     * @return A log message ready to be written to the log
     */
    default public String builder(String callData){
        return timestamp()+" "+this.getClass().getSimpleName()+"["+callData+"]";
    }

    /**
     * This is the timestamp method, it has a timestamp that is formatted like this: 05/12/2022 - 10:57
     * @return Returns the formatted timestamp with square brackets, so the returned value will be "[05/12/2022 - 10:57]"
     */
    default public String timestamp(){
        return "["+LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"))+"]";

    }
}

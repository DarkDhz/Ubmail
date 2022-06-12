package es.arnaugris.smtp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleLogger {

    private final int id;
    private final boolean debug = true;
    public ConsoleLogger() {
        IDManager idManager = IDManager.getInstance();
        this.id = idManager.getNextID();
    }

    /**
     * Method to get actual time
     * @return Actual timestamp
     */
    private String getActualTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    /**
     * Method to print start message
     */
    public void printStart() {
        if (debug) System.out.println("(" + this.getActualTime() +  " ID " + this.id + ") STARTED HANDLING NEW REQUEST");
    }

    /**
     * Method to print received message
     * @param from Mail sender
     */
    public void printReceived(String from) {
        if (debug) System.out.println("(" + this.getActualTime() +  " ID " + this.id + ") MAIL RECEIVED FROM " + from);
    }

    /**
     * Method to print report sent message
     * @param from Mail destination
     */
    public void printSent(String from) {
        if (debug) System.out.println("(" + this.getActualTime() +  " ID " + this.id + ") REPORT SENT TO " + from);
    }
}

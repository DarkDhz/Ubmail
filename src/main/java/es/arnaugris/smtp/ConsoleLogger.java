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

    private String getActualTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public void printStart() {
        if (debug) System.out.println("(" + this.getActualTime() +  " ID " + this.id + ") STARTED HANDLING NEW REQUEST");
    }

    public void printReceived(String from) {
        if (debug) System.out.println("(" + this.getActualTime() +  " ID " + this.id + ") MAIL RECEIVED FROM " + from);
    }

    public void printSent(String from) {
        if (debug) System.out.println("(" + this.getActualTime() +  " ID " + this.id + ") REPORT SENT TO " + from);
    }
}

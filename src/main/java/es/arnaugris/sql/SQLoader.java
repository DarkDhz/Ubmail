package es.arnaugris.sql;

import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SQLoader implements Runnable{

    @Override
    public void run() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

                           @Override
                           public void run() {
                               System.out.println("LOADING SQL DATA");
                               SQLUtils sqlUtils = SQLUtils.getInstance();
                               try {
                                   sqlUtils.loadDomains();
                                   sqlUtils.loadBanned();
                                   System.out.println("SQL DATA LOAD SUCCESS");
                               } catch (SQLException | ClassNotFoundException exception) {
                                   System.out.println(exception.getMessage());
                               }
                           }
                       },
                // starting now, 5 minutes
                new Date(), 300000L

        );

    }
}

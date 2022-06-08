package es.arnaugris.sql;

import java.sql.SQLException;
import java.util.ArrayList;
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
                               SQLUtils sqlUtils = SQLUtils.getInstance();
                               try {
                                   sqlUtils.loadDomains();
                                   sqlUtils.loadBanned();
                               } catch (SQLException | ClassNotFoundException ignored) {}
                           }
                       },
                // starting now, 5 minutes
                new Date(), 300000L

        );

    }
}

package es.arnaugris.utils;

import java.sql.Array;
import java.util.ArrayList;

public class MailData {

    private String mail_from;
    private final ArrayList<String> mail_to;
    private final ArrayList<Object> data;

    public MailData() {
        mail_to = new ArrayList<String>();
        data = new ArrayList<Object>();
    }

    public String getMail_from() {
        return mail_from;
    }

    public void clearAndSetMail_from(String message) {
        String result = message.replaceAll("MAIL FROM:", "");
        result = result.replaceAll(">", "");
        result = result.replaceAll("<", "");
        this.mail_from = result;

    }

    public void clearAndAddMail_to(String message) {
        String result = message.replaceAll("RCPT TO:", "");
        result = result.replaceAll(">", "");
        result = result.replaceAll("<", "");
        mail_to.add(result);
    }

    public ArrayList<String> getMailTo() {
        return this.mail_to;
    }

    public void addData(String message) {
        data.add(message);
    }

    public ArrayList<Object> getData() {
        return this.data;
    }



}

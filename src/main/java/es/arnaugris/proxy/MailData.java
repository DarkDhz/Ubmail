package es.arnaugris.proxy;

import java.sql.Array;
import java.util.ArrayList;

public class MailData {

    private String mail_from;
    private ArrayList<String> mail_to;
    private String data;

    public MailData() {
        mail_to = new ArrayList<String>();
    }

    public String getMail_from() {
        return mail_from;
    }

    public void setMail_from(String mail_from) {
        this.mail_from = mail_from;
    }

    public void addReciver(String user) {
        mail_to.add(user);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }



}

package es.arnaugris.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

public class MailData {

    private String mail_from;
    private final ArrayList<String> mail_to;
    private final ArrayList<Object> data;
    private String username;
    private String password;


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
        System.out.println(message);
        data.add(message);
    }

    public ArrayList<Object> getData() {
        return this.data;
    }

    public String exportMessage() {
        return "TODO";
    }

    public void auth(String encoded) {
        encoded = encoded.replaceAll("AUTH PLAIN ", "");

        byte[] decoded = Base64.getDecoder().decode(encoded);
        
        // replace null for space
        for (int i = 0; i < decoded.length; i++) {
            if (decoded[i] == 0) {
                decoded[i] = 32;
            }
        }

        String result = new String(decoded, StandardCharsets.UTF_8);

        String[] data = result.split(" ");

        this.username = data[1];
        this.password = data[2];
    }

    public String getCredentials() {
        return "mail: " + this.username + " pass: " + this.password;
    }

}

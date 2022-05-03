package es.arnaugris.utils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

public class MailData {

    private String mail_from;
    private final ArrayList<String> mail_to;
    private final ArrayList<Object> data;
    private String username;
    private String password;
    private String content;
    private ArrayList<String> urls;


    public MailData() {
        mail_to = new ArrayList<String>();
        data = new ArrayList<Object>();
        urls = new ArrayList<String>();
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

    public int checkBlacklist() {
        BlackListUtils blackListUtils = BlackListUtils.getInstance();
        for (String uri : this.urls) {
            String domain = extractDomain(uri);
            try {
                blackListUtils.checkDomain(domain);
            } catch (IOException e) {
                return -1;
            }
        }
        return -1;
    }

    private String extractDomain(String url) {
        String uri = url.split("//")[1];
        uri = uri.split("/")[0];
        return uri;
    }

    public String getCredentials() {
        return "mail: " + this.username + " pass: " + this.password;
    }

    public String extractMessage() {
        //System.out.println(data);
        String boundary = null;
        String end_boundary = null;
        boolean reading = false;
        String message = "";

        for (Object line : data) {
            if (line instanceof String) {
                String line_string = (String) line;
                if (boundary != null) {
                    if (reading) {
                        if (line_string.contains(end_boundary)) {
                            reading = false;
                        } else {
                            extractURL(line_string);
                            message = message + line_string + "\n";
                        }
                    } else {
                        if (line_string.contains(boundary)) {
                            reading = true;
                        }
                    }
                } else if (line_string.contains("boundary")) {
                    boundary = line_string.split("boundary=")[1].replaceAll("\"", "");
                    end_boundary = boundary + "--";
                }
            }
        }
        this.content = message;
        return message;
    }

    public ArrayList<String> getURLs() {
        return this.urls;
    }

    private void extractURL(String line) {
        for (String word : line.split(" ")) {
            try {
                URL url = new URL(word);
                urls.add(word);
            } catch (Exception ignored) {}
        }
    }
}

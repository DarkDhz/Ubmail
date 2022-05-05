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

    /**
     * Default class builder
     */
    public MailData() {
        mail_to = new ArrayList<String>();
        data = new ArrayList<Object>();
        urls = new ArrayList<String>();
    }

    /**
     * Obtain sender of mail
     * @return sender of mail
     */
    public String getMail_from() {
        return mail_from;
    }

    /**
     * Method to get the sender of mail
     * @param message SMTP message line
     */
    public void clearAndSetMail_from(String message) {
        String result = message.replaceAll("MAIL FROM:", "");
        result = result.replaceAll(">", "");
        result = result.replaceAll("<", "");
        this.mail_from = result;
    }

    /**
     * Method to get receivers of the mail
     * @param message SMTP message line
     */
    public void clearAndAddMail_to(String message) {
        String result = message.replaceAll("RCPT TO:", "");
        result = result.replaceAll(">", "");
        result = result.replaceAll("<", "");
        mail_to.add(result);
    }

    /**
     * Method to get receivers of the mail
     * @return ArrayList with all the receivers
     */
    public ArrayList<String> getMailTo() {
        return this.mail_to;
    }

    /**
     * Method to add body message
     * @param message SMTP message line
     */
    public void addData(String message) {
        data.add(message);
    }

    /**
     * Method to get the body of the mail (message)
     * @return List of message lines
     */
    public ArrayList<Object> getData() {
        return this.data;
    }

    /**
     * Method to decode auth in base64
     * @param encoded encoded base64 SMTP auth message;
     */
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

    /**
     * Method to check if mail is dangerous
     * @return True if dangerous, False otherwise
     */
    public boolean checkBlacklist() throws IOException {
        BlackListUtils blackListUtils = BlackListUtils.getInstance();
        for (String uri : this.urls) {
            String domain = extractDomain(uri);
            try {
                blackListUtils.checkDomain(domain);
            } catch (IOException e) {
                throw new IOException("Canno't check the url");
            }
        }
        return false;
    }

    /**
     * Method to extract domain from an URL
     * @param url The URL
     * @return The domain of the URL
     */
    private String extractDomain(String url) {
        String uri = url.split("//")[1];
        uri = uri.split("/")[0];
        return uri;
    }

    /**
     * Method to get the auth credentials
     * @return auth credentials
     */
    public String getCredentials() {
        return "mail: " + this.username + " pass: " + this.password;
    }

    /**
     * Method to get the boundary and then extract the mail message
     * @return The mail message
     */
    public String extractMessage() {
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

    /**
     * Method to get the URL's
     * @return ArrayList of URL's
     */
    public ArrayList<String> getURLs() {
        return this.urls;
    }

    /**
     * Method to extract URL's from messages
     * @param line Line to check
     */
    private void extractURL(String line) {
        for (String word : line.split(" ")) {
            try {
                URL url = new URL(word);
                urls.add(word);
            } catch (Exception ignored) {}
        }
    }

    public void clear() {
        mail_from = "";
        mail_to.clear();
        data.clear();
        username = "";
        password = "";
        content = "";
        urls.clear();
    }
}

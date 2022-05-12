package es.arnaugris.utils;

import es.arnaugris.external.DomainList;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MailData {

    private String mail_from;
    private final ArrayList<String> mail_to;
    private final ArrayList<Object> data;
    private String username;
    private String password;
    private String content;
    private ArrayList<String> urls;
    private Map<String, Boolean> blacklist;
    private Map<String, Boolean> shorten;
    private Map<String, String> similar;

    /**
     * Default class builder
     */
    public MailData() {
        mail_to = new ArrayList<String>();
        data = new ArrayList<Object>();
        urls = new ArrayList<String>();
        shorten = new HashMap<>();
        blacklist = new HashMap<>();
        similar = new HashMap<>();
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
    public void checkBlacklist() {
        BlackListUtils blackListUtils = BlackListUtils.getInstance();
        for (String uri : this.urls) {
            String domain = extractDomain(uri);
            try {
                blackListUtils.checkDomain(domain);
            } catch (IOException e) {
                this.blacklist.put(uri, null);
            }
        }
    }

    /**
     * Check if any URL has been shortened
     */
    public void checkShortenURLService() {
        BlackListUtils blackListUtils = BlackListUtils.getInstance();
        this.shorten.clear();
        for (String uri : this.urls) {
           this.shorten.put(uri, blackListUtils.checkShorteneer(uri));
        }
    }

    /**
     * Check all options
     */
    public void checkAll() {
        this.similar.clear();
        this.shorten.clear();
        this.blacklist.clear();

        BlackListUtils blackListUtils = BlackListUtils.getInstance();
        Levenshtein lev = Levenshtein.getInstance();
        DomainList domains = DomainList.getInstance();

        for (String uri : this.urls) {
            this.shorten.put(uri, blackListUtils.checkShorteneer(uri));
            Map<String, String> result = convertBlacklistToMap("{\"status\":\"Not blacklisted\",\"blacklist_cnt\":0,\"blacklist_severity\":\"\",\"API_calls_remaining\":186,\"response\":\"OK\",\"blacklists\":[]}");

            String domain = extractDomain(uri);
            /*try {
                blackListUtils.checkDomain(domain);
            } catch (IOException e) {
               this.blacklist.put(uri, null);
            }*/

            int min_distance = 999999999;

            String most_similar = "None";

            for (String check : domains.getList()) {

                int distance = lev.levenshtein(check, domain, false);

                if ((distance < min_distance) && (distance != 0 ) && (distance < 10)) {
                    most_similar = check;
                }
            }

            similar.put(domain, most_similar);

        }

    }

    // change to string to JSON TODO
    private Map<String, String> convertBlacklistToMap(String message) {
        message = message.substring(1, message.length()-1);
        String[] keyValuePairs = message.split(",");
        Map<String,String> map = new HashMap<>();

        for(String pair : keyValuePairs) {
            String[] entry = pair.split(":");
            map.put(entry[0].trim(), entry[1].trim());
        }

        return map;
    }

    public Map<String, Boolean> getBlacklist() {
        return this.blacklist;
    }

    public Map<String, Boolean> getShorten() {
        return this.shorten;
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

    public void checkDomainDistance() {
        this.similar.clear();

        Levenshtein lev = Levenshtein.getInstance();
        DomainList domains = DomainList.getInstance();

        for (String url : this.urls) {
            url = extractDomain(url);

            int min_distance = 999999999;

            String most_similar = "None";

            for (String domain : domains.getList()) {

                int distance = lev.levenshtein(domain, url, false);

                if ((distance < min_distance) && (distance != 0 ) && (distance < 10)) {
                    most_similar = domain;
                }
            }

            similar.put(url, most_similar);

        }
    }

    public Map<String, String> getSimilarityDomains() {
        return this.similar;
    }


}

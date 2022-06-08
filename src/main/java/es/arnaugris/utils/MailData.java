package es.arnaugris.utils;

import es.arnaugris.external.DomainYaml;
import es.arnaugris.utils.checks.BlackListUtils;
import es.arnaugris.utils.checks.Levenshtein;
import es.arnaugris.utils.smtp.ReportGenerator;

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

    private String message;
    private final ArrayList<String> urls;
    private final Map<String, Boolean> blacklist;
    private final Map<String, String> shorten;
    private final Map<String, String> similar;
    private final Map<String, Boolean> banned;

    private final ArrayList<String> hidden;


    /**
     * Default class builder
     */
    public MailData() {
        mail_to = new ArrayList<>();
        data = new ArrayList<>();
        urls = new ArrayList<>();
        shorten = new HashMap<>();
        blacklist = new HashMap<>();
        similar = new HashMap<>();
        banned = new HashMap<>();
        hidden = new ArrayList<>();
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
     * Method to add body message
     * @param message SMTP message line
     */
    public void addData(String message) {
        data.add(message);
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
     * Check all options
     */
    public void checkAll() {
        this.similar.clear();
        this.shorten.clear();
        this.blacklist.clear();
        this.banned.clear();

        BlackListUtils blackListUtils = BlackListUtils.getInstance();
        Levenshtein lev = Levenshtein.getInstance();
        DomainYaml domainYaml = DomainYaml.getInstance();


        ArrayList<String> copy = new ArrayList<>();
        for (String uri : this.urls) {
            if (blackListUtils.checkShortener(uri)) {
                String real_url = blackListUtils.getRealURL(uri);

                if (!uri.equalsIgnoreCase(real_url)) {
                    this.shorten.put(uri, real_url);
                    copy.add(real_url);
                }
            }
        }

        this.urls.addAll(copy);

        for (String uri : this.urls) {

            System.out.println(uri);

            String domain;

            try {
                domain = extractDomain(uri);
            } catch (Exception e) {
                break;
            }


            try {
                //TODO CHANGE FOR DELIVERY
                //Map<String, String> blacklistJSON = blackListUtils.checkDomain(domain);
                Map<String, String> blacklistJSON = blackListUtils.fakeDomain();
                blacklist.put(domain, Integer.parseInt(blacklistJSON.get("blacklist_cnt")) > 0);
            } catch (IOException e) {
               this.blacklist.put(uri, false);
            }



            int min_distance = Integer.MAX_VALUE;
            String most_similar = "None";

            for (String check : domainYaml.getList()) {


                int distance = lev.levenshtein(check, domain);

                if (distance == 0) {
                    most_similar = "Legitimate link";
                    break;
                }

                if ((distance < min_distance) && (distance < domainYaml.getSensitive())) {
                    most_similar = check;
                }
            }

            similar.put(domain, most_similar);

            for (String bannedURI : domainYaml.getBanned()) {
                if (bannedURI.equalsIgnoreCase(domain)) {
                    this.banned.put(domain, true);
                }
            }

        }

    }


    /**
     * Method to extract domain from a URL
     * @param url The URL
     * @return The domain of the URL
     */
    private String extractDomain(String url) throws Exception {
        String[] splited = url.split("//");
        if (splited.length == 0) {
            throw new Exception("Not real domain");
        }
        String uri = splited[1];
        uri = uri.split("/")[0];
        return uri;
    }


    /**
     * Method to get the boundary and then extract the mail message
     * @return The mail message
     */
    public String extractMessage() {
        String boundary = null;
        String end_boundary = null;
        boolean reading = false;
        StringBuilder message = new StringBuilder();

        for (Object line : data) {
            if (line instanceof String) {
                String line_string = (String) line;
                if (boundary != null) {
                    if (reading) {
                        if (line_string.contains(end_boundary)) {
                            reading = false;
                        } else {
                            extractURL(line_string);
                            message.append(line_string).append("\n");
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
        return message.toString();
    }


    /**
     * Method to extract URLs from messages
     * @param line Line to check
     */
    private void extractURL(String line) {
        boolean hidden = false;
        line = line.replaceAll(">", " ").replaceAll("<", " ");
        for (String word : line.split(" ")) {
            if (word.contains("href")) {
                word = word.replaceAll("href=3D", "");
                word = word.replaceAll("href=", "");
                word = word.replaceAll("\"", "");
                hidden = true;
            }
            try {
                URL url = new URL(word);
                if ((word.contains("https:=")) || (word.contains("mailto:")) || (word.contains("tlf:"))) {
                    continue;
                }
                if (hidden) {
                    this.hidden.add(word);
                }
                urls.add(word.replaceAll(" ", ""));
            } catch (Exception ignored) {}
        }
    }

    public String getReport() {
        this.message = this.extractMessage();
        this.checkAll();

        ReportGenerator reportGenerator = new ReportGenerator(this);
        return reportGenerator.generateHTMLReport();
    }

    public void clear() {
        mail_from = "";
        mail_to.clear();
        data.clear();
        username = "";
        password = "";
        urls.clear();
        banned.clear();
        hidden.clear();
    }

    public Map<String, Boolean> getBanned() {
        return this.banned;
    }

    public ArrayList<String> getHidden() { return this.hidden; }

    /**
     * Method to get the URLs
     * @return ArrayList of URLs
     */
    public ArrayList<String> getURLs() {
        return this.urls;
    }

    /**
     * Method to get Similar domains
     * @return Map of similarities
     */
    public Map<String, String> getSimilarityDomains() {
        return this.similar;
    }

    /**
     * Method to get the blacklist result from HURL'S
     * @return Map of URLs
     */
    public Map<String, Boolean> getBlacklist() {
        return this.blacklist;
    }

    /**
     * Method to get URLs with shorten service
     * @return Map of URLs
     */
    public Map<String, String> getShorten() {
        return this.shorten;
    }

    /**
     * Method to get the body of the mail (message)
     * @return List of message lines
     */
    public ArrayList<Object> getData() {
        return this.data;
    }

    /**
     * Obtain sender of mail
     * @return sender of mail
     */
    public String getMailFrom() {
        return mail_from;
    }

    /**
     * Method to get receivers of the mail
     * @return ArrayList with all the receivers
     */
    public ArrayList<String> getMailTo() {
        return this.mail_to;
    }

    /**
     * Method to get body message
     * @return The body message
     */
    public String getMessage() {
        return this.message;
    }


}

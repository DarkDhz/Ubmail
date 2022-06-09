package es.arnaugris.utils;

import es.arnaugris.external.DomainYaml;
import es.arnaugris.sql.SQLUtils;
import es.arnaugris.utils.checks.BlackListUtils;
import es.arnaugris.utils.checks.Levenshtein;
import es.arnaugris.utils.smtp.ReportGenerator;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MailData {

    private String mail_from;
    private final ArrayList<String> mail_to;
    private final ArrayList<String> data;
    private String username;
    private String password;
    private String message;
    private final ArrayList<String> urls;
    private final Map<String, Boolean> blacklist;
    private final Map<String, String> shorten;
    private final Map<String, String> similar;
    private final Map<String, Boolean> banned;
    private final ArrayList<String> hidden;
    private final ArrayList<String> shorten_urls;

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
        shorten_urls = new ArrayList<>();
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
        SQLUtils sqlUtils = SQLUtils.getInstance();


        ArrayList<String> copy = new ArrayList<>();
        for (String uri : this.urls) {
            if (blackListUtils.checkShortener(uri)) {
                String real_url;
                try {
                    real_url = blackListUtils.getRealURLV2(uri);
                    if (real_url == null) {
                        continue;
                    }

                    if (!uri.equalsIgnoreCase(real_url)) {
                        this.shorten_urls.add(uri);
                        this.shorten.put(uri, real_url);
                        copy.add(real_url);
                    }
                } catch (IOException ignored) {}
            }
        }

        this.urls.addAll(copy);

        for (String uri : this.urls) {

            String domain;

            try {
                domain = extractDomainV2(uri);
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

            for (String check : sqlUtils.getList()) {


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

            for (String bannedURI : sqlUtils.getBanned()) {
                if (bannedURI.equalsIgnoreCase(domain) || bannedURI.contains(domain)) {
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
    /*private String extractDomain(String url) throws Exception {
        String[] split = url.split("//");

        if (split.length == 0) {
            throw new Exception("Not real domain");
        }

        String uri = split[1];
        uri = uri.split("/")[0];
        System.out.println(uri);

        String inte = "?";
        if (uri.contains(inte)) {
            uri = url.split(inte)[0];
        }
        System.out.println(uri);
        System.out.println("done");
        return uri;
    }*/

    private String extractDomainV2(String url) throws Exception {
        String[] split = url.split("//");

        if (split.length == 0) {
            throw new Exception("Not real domain");
        }

        URL uri = new URL(url);
        return uri.getHost();
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

        StringBuilder completeUrl = null;
        for (String line_string : data) {

            if (boundary != null) {

                if (reading) {
                    if (line_string.contains(end_boundary)) {
                        reading = false;
                        boundary = null;
                    } else {

                        String check = line_string.replaceAll("/r", "").replaceAll("/n", "").replaceAll(" ", "");

                        String substring = "";

                        if (check.length() > 1) {
                             substring = check.trim().substring(check.length() - 1);
                        }




                        if (check.contains("http")  && completeUrl == null && substring.equalsIgnoreCase("=")) {

                            completeUrl = new StringBuilder();

                            String[] split = line_string.split(" ");
                            String word = split[split.length - 1];

                            word = word.replaceAll("/r", "").replaceAll("/n", "").replaceAll(" ", "").replaceAll("=3D", "=");

                            StringBuilder st = new StringBuilder(word);
                            st.deleteCharAt(st.length()-1);

                            completeUrl.append(st);

                        } else if (completeUrl != null){
                            if (line_string.contains(" ") || line_string.contains("<") || line_string.contains("/r") || line_string.contains("/n")) {
                                String line = line_string.replaceAll(" ", "<");

                                completeUrl.append(line.split("<")[0]);
                                extractURL(completeUrl.toString());

                                completeUrl = null;
                            } else {
                                line_string = line_string.replaceAll("=3D", "=");

                                if (line_string.trim().substring(line_string.length()-1).equalsIgnoreCase("=")) {
                                    StringBuilder st = new StringBuilder(line_string);
                                    st.deleteCharAt(st.length()-1);
                                    completeUrl.append(st);
                                } else {

                                    completeUrl.append(line_string);
                                }

                            }

                        }

                        if (line_string.contains(" ") || line_string.contains("<") || line_string.contains("/r") || line_string.contains("/n")) {
                            if (completeUrl == null) {
                                extractURL(line_string);
                            }

                        }

                        if (check.length() > 2 && substring.equalsIgnoreCase("=")) {
                            StringBuilder st = new StringBuilder(line_string);
                            st.deleteCharAt(st.length()-1);
                            line_string = st.toString();
                        }

                        message.append(line_string);


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
        return message.toString();
    }




    /**
     * Method to extract URLs from messages
     * @param line Line to check
     */
    private void extractURL(String line) {
        boolean hidden = line.contains("<") && line.contains(">");

        line = line.replaceAll(">", " ").replaceAll("<", " ");

        for (String word : line.split(" ")) {
            if (word.contains("href")) {
                word = word.replaceAll("href=3D", "");
                word = word.replaceAll("href=", "");
                word = word.replaceAll("\"", "");
                hidden = true;
            }

            try {
                new URL(word);
                if ((word.contains("https:=")) || (word.contains("mailto:")) || (word.contains("tlf:"))) {
                    continue;
                }

                word = word.replaceAll(" ", "");

                if (!urls.contains(word)) {
                    urls.add(word);
                    if (hidden) {
                        this.hidden.add(word);
                    }
                }

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

    public ArrayList<String> getShorten_urls() {
        return shorten_urls;
    }

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
    public ArrayList<String> getData() {
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
     * Method to get body message
     * @return The body message
     */
    public String getMessage() {
        return this.message;
    }



}

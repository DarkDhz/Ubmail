package es.arnaugris.smtp;

import es.arnaugris.external.DomainYaml;
import es.arnaugris.sql.SQLUtils;
import es.arnaugris.checks.BlackListUtils;
import es.arnaugris.checks.Levenshtein;

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
    private final SMTPExtractor smtpExtractor;
    private final SMTPChecker smtpChecker;

    public MailData() {
        mail_to = new ArrayList<>();
        data = new ArrayList<>();
        smtpExtractor = new SMTPExtractor();
        smtpChecker = new SMTPChecker();
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
     * Method to process the data report
     * @return HTML Report
     */
    public String getReport() {
        this.message = smtpExtractor.extractMessage(this.data);
        ArrayList<String> result = smtpChecker.discoverURLS(getURLs());
        smtpExtractor.addURLs(result);
        smtpChecker.checkAll(getURLs());

        ReportGenerator reportGenerator = new ReportGenerator(this);
        return reportGenerator.generateHTMLReport();
    }

    /**
     * Method to reset data
     */
    public void clear() {
        mail_from = "";
        mail_to.clear();
        data.clear();
        username = "";
        password = "";
        smtpExtractor.clear();
        smtpChecker.clear();
    }

    /**
     * Method to get banned result
     * @return Result Map
     */
    public Map<String, Boolean> getBanned() {
        return smtpChecker.getBanned();
    }

    /**
     * Method to get Hidden URL
     * @return Hidden URL
     */
    public ArrayList<String> getHidden() { return smtpExtractor.getHidden(); }

    public ArrayList<String> getShorten_urls() {
        return smtpChecker.getShorten_urls();
    }

    /**
     * Method to get the URLs
     * @return ArrayList of URLs
     */
    public ArrayList<String> getURLs() {
        return smtpExtractor.getUrls();
    }

    /**
     * Method to get Similar domains
     * @return Map of similarities
     */
    public Map<String, String> getSimilarityDomains() {
        return smtpChecker.getSimilar();
    }

    /**
     * Method to get the blacklist result from HURL'S
     * @return Map of URLs
     */
    public Map<String, Boolean> getBlacklist() {
        return smtpChecker.getBlacklist();
    }

    /**
     * Method to get URLs with shorten service
     * @return Map of URLs
     */
    public Map<String, String> getShorten() {
        return smtpChecker.getShorten();
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

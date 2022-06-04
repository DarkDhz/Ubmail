package es.arnaugris.utils.smtp;

import es.arnaugris.utils.MailData;

import java.util.Map;

public class ReportGenerator {
    // Singleton Instance
    private static volatile ReportGenerator instance = null;

    private MailData data;

    public ReportGenerator(MailData d) {
        this.data = d;
    }

    public String generateHTMLReport() {

        String toReturn = this.generateHeader() +
                this.generateBlacklist() +
                this.generateBanned() +
                this.generateSimilar() +
                this.generateShorten() +
                this.generateURLS();

        return toReturn;
    }

    private String generateHeader() {
        return "<h4>REPORT FROM <a style=\"color: green;\"> ANTI PHISHING AG.ES </a>\n</h4>";

    }

    private String generateBlacklist() {
        StringBuilder toReturn = new StringBuilder("<p>----------------- BLACKLIST -----------------\n");

        Map<String, Boolean> black_list = data.getBlacklist();

        for (Map.Entry<String, Boolean> entry : black_list.entrySet()) {
            if (entry.getValue()) {
                toReturn.append("\n</p><p>");
                toReturn.append("URL ").append(entry.getKey()).append(" ").append(" IS <a style=\"color: red;\">BLACKLISTED</a>").append(" \n");
            }
        }
        toReturn.append("\n</p>");
        return toReturn.toString();
    }

    private String generateBanned() {
        StringBuilder toReturn = new StringBuilder("<p>----------------- BANNED -----------------\n");

        Map<String, Boolean> banned_list = data.getBanned();

        for (Map.Entry<String, Boolean> entry : banned_list.entrySet()) {
            toReturn.append("\n</p><p>");
            toReturn.append("URL ").append(entry.getKey()).append(" ").append(entry.getValue()).append(" \n");
        }
        toReturn.append("\n</p>");
        return toReturn.toString();
    }

    private String generateSimilar() {
        StringBuilder toReturn = new StringBuilder("<p>----------------- SIMILAR -----------------\n");

        Map<String, String> similar = data.getSimilarityDomains();

        for (Map.Entry<String, String> entry : similar.entrySet()) {
            toReturn.append("\n</p><p>");

            if (entry.getValue().equalsIgnoreCase("None")) {
                toReturn.append(entry.getKey()).append(" no similarity ").append("\n");
            } else if (entry.getValue().equalsIgnoreCase("Legitimate link")) {
                toReturn.append(entry.getKey()).append(" <a style=\"color: green;\">is legitim link</a> ").append("\n");
            } else {
                toReturn.append(entry.getKey()).append(" <a style=\"color: red;\">similar</a> to ").append(entry.getValue()).append("\n");
            }

        }
        toReturn.append("\n</p>");
        return toReturn.toString();
    }

    private String generateShorten() {
        StringBuilder toReturn = new StringBuilder("<p>----------------- SHORTEN -----------------");

        Map<String, Boolean> shorten = data.getShorten();

        for (Map.Entry<String, Boolean> entry : shorten.entrySet()) {
            if (entry.getValue()) {
                toReturn.append("\n</p><p>");
                toReturn.append("URL ").append(entry.getKey()).append(" ").append(" IS <a style=\"color: red;\">SHORTENED</a>").append(" \n");
            }
        }
        toReturn.append("\n</p>");
        return toReturn.toString();

    }

    private String generateURLS() {
        StringBuilder toReturn = new StringBuilder("<p>----------------- URLS -----------------\n");
        for (String uri : data.getURLs()) {
            toReturn.append("\n</p><p>");
            toReturn.append(uri).append(" \n");
        }
        toReturn.append("\n</p>");
        return toReturn.toString();
    }


}

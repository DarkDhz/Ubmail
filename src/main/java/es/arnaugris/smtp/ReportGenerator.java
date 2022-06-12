package es.arnaugris.smtp;

import java.util.Map;

public class ReportGenerator {

    private final MailData data;

    public ReportGenerator(MailData d) {
        this.data = d;
    }

    /**
     * Method to generate the full report
     * @return Full report
     */
    public String generateHTMLReport() {

        return this.generateHeader() +
                this.generateSummary() +
                this.generateSimilar() +
                this.generateHidden() +
                this.generateBlacklist() +
                this.generateBanned() +
                this.generateShorten() +
                this.generateShortenCorrespondences() +
                this.generateURLS();
    }

    /**
     * Method to generate report header
     * @return The report header
     */
    private String generateHeader() {
        return "<h4>REPORT FROM <a style=\"color: green;\"> ANTI PHISHING AG.ES </a>\n</h4>";

    }

    /**
     * Method to generate report summary
     * @return The report summary
     */
    private String generateSummary() {
        StringBuilder toReturn = new StringBuilder("<p>----------------- SUMMARY -----------------\n");

        int urls = data.getURLs().size();
        toReturn.append("\n</p><p>");
        toReturn.append("NUMBER OF URLS: ").append(urls);
        toReturn.append("\n</p><p>");
        toReturn.append("HIDDEN URLS: ").append(data.getHidden().size());
        toReturn.append("\n</p><p>");
        toReturn.append("SHORTEN URLS: ").append(data.getShorten_urls().size()).append("/").append(urls);
        toReturn.append("\n</p><p>");
        toReturn.append("BLACKLISTED DOMAINS: ").append(data.getBlacklist().size());
        toReturn.append("\n</p><p>");
        toReturn.append("BANNED DOMAINS: ").append(data.getBanned().size());
        toReturn.append("\n</p>");
        return toReturn.toString();
    }

    /**
     * Method to generate hidden report section
     * @return Hidden report
     */
    private String generateHidden() {
        StringBuilder toReturn = new StringBuilder("<p>----------------- HIDDEN -----------------\n");

        for (String uri : data.getHidden()) {
            toReturn.append("\n</p><p>");
            toReturn.append(uri).append(" NOT APPEARS AS MAIL TEXT \n");
        }
        toReturn.append("\n</p>");
        return toReturn.toString();
    }

    /**
     * Method to generate blacklist report section
     * @return Blacklist report
     */
    private String generateBlacklist() {
        StringBuilder toReturn = new StringBuilder("<p>----------------- BLACKLIST -----------------\n");

        Map<String, Boolean> black_list = data.getBlacklist();

        for (Map.Entry<String, Boolean> entry : black_list.entrySet()) {
            if (entry.getValue()) {
                toReturn.append("\n</p><p>");
                toReturn.append("URL ").append(entry.getKey()).append(" ").append(" IS <strong><a style=\"color: red;\">BLACKLISTED</a></strong>").append(" \n");
            }
        }
        toReturn.append("\n</p>");
        return toReturn.toString();
    }

    /**
     * Method to generate Banned report section
     * @return Banned report
     */
    private String generateBanned() {
        StringBuilder toReturn = new StringBuilder("<p>----------------- BANNED -----------------\n");

        Map<String, Boolean> banned_list = data.getBanned();

        for (Map.Entry<String, Boolean> entry : banned_list.entrySet()) {
            if (entry.getValue()) {
                toReturn.append("\n</p><p>");
                toReturn.append("URL ").append(entry.getKey()).append(" ").append(" IS <strong><a style=\"color: red;\">BANNED</a></strong>").append(" \n");
            }
        }
        toReturn.append("\n</p>");
        return toReturn.toString();
    }

    /**
     * Method to generate Similar report section
     * @return Similar report
     */
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
                toReturn.append(entry.getKey()).append(" <strong><a style=\"color: red;\">SIMILAR</a></strong> ").append(entry.getValue()).append("\n");
            }

        }
        toReturn.append("\n</p>");
        return toReturn.toString();
    }

    /**
     * Method to generate Correspondences report section
     * @return Correspondences report
     */
    private String generateShortenCorrespondences() {
        StringBuilder toReturn = new StringBuilder("<p>----------------- REAL URLS -----------------");

        Map<String, String> shorten = data.getShorten();

        for (Map.Entry<String, String> entry : shorten.entrySet()) {
            toReturn.append("\n</p><p>");
            toReturn.append("URL ").append(entry.getKey()).append(" ").append(" IN REAL IS ").append(entry.getValue()).append(" \n");
        }
        toReturn.append("\n</p>");
        return toReturn.toString();

    }

    /**
     * Method to generate Shorten report section
     * @return Shorten report
     */
    private String generateShorten() {
        StringBuilder toReturn = new StringBuilder("<p>----------------- SHORTEN -----------------");

        for (String uri : data.getShorten_urls()) {
            toReturn.append("\n</p><p>");
            toReturn.append(uri).append(" IS USING <strong><a style=\"color: red;\">SHORTEN SERVICE</a>").append(" \n");
        }
        toReturn.append("\n</p>");
        return toReturn.toString();

    }

    /**
     * Method to generate URL report section
     * @return URL report
     */
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

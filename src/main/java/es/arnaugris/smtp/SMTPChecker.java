package es.arnaugris.smtp;

import es.arnaugris.checks.BlackListUtils;
import es.arnaugris.checks.Levenshtein;
import es.arnaugris.external.DomainYaml;
import es.arnaugris.sql.SQLUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SMTPChecker {

    private final Map<String, Boolean> blacklist;
    private final Map<String, String> shorten;
    private final Map<String, String> similar;
    private final Map<String, Boolean> banned;
    private final ArrayList<String> shorten_urls;

    public SMTPChecker() {
        shorten = new HashMap<>();
        blacklist = new HashMap<>();
        similar = new HashMap<>();
        banned = new HashMap<>();
        shorten_urls = new ArrayList<>();

    }

    /**
     * Method to discover redirections
     * @param urls URL to discover
     * @return Discovered URL
     */
    public ArrayList<String> discoverURLS(ArrayList<String> urls) {

        BlackListUtils blackListUtils = BlackListUtils.getInstance();
        ArrayList<String> copy = new ArrayList<>();

        for (String uri : urls) {
            if (blackListUtils.checkShortener(uri) || uri.contains("ct.sendgrid.net")) {
                if (!uri.contains("ct.sendgrid.net")) {
                    this.shorten_urls.add(uri);
                }

                String real_url;
                try {
                    real_url = blackListUtils.getRealURL(uri);

                    if (real_url == null) {
                        real_url = blackListUtils.getRealURLV2(uri);


                        if (real_url == null) {
                            continue;
                        }

                    }

                    if (!uri.equalsIgnoreCase(real_url) && !real_url.equals("")) {
                        this.shorten.put(uri, real_url);
                        copy.add(real_url);
                    }



                } catch (IOException ignored) {}
            }
        }
        return copy;

    }

    /**
     * Method to check if domain is blacklisted
     * @param domain Domain to check
     */
    private void checkBlacklist(String domain) {
        BlackListUtils blackListUtils = BlackListUtils.getInstance();
        try {
            //TODO CHANGE FOR DELIVERY
            Map<String, String> blacklistJSON = blackListUtils.checkDomain(domain);
            //Map<String, String> blacklistJSON = blackListUtils.fakeDomain();
            blacklist.put(domain, Integer.parseInt(blacklistJSON.get("blacklist_cnt")) > 0);
        } catch (IOException ignored) {

        }
    }

    /**
     * Method to check similar domains
     * @param domain Domain to check
     */
    private void checkSimilar(String domain) {
        Levenshtein lev = Levenshtein.getInstance();
        DomainYaml domainYaml = DomainYaml.getInstance();
        SQLUtils sqlUtils = SQLUtils.getInstance();

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

    }

    /**
     * Method to check if domain is banned
     * @param domain Domain to check
     */
    private void checkBanned(String domain) {
        SQLUtils sqlUtils = SQLUtils.getInstance();

        for (String bannedURI : sqlUtils.getBanned()) {
            if (bannedURI.equalsIgnoreCase(domain) || domain.contains(bannedURI)) {
                this.banned.put(domain, true);
            }
        }
    }

    /**
     * Method to check all modules
     * @param urls URL to check
     */
    public void checkAll(ArrayList<String> urls) {
        ArrayList<String> domains = new ArrayList<>();

        for (String uri : urls) {
            String domain;
            try {
                domain = this.extractDomainV2(uri);
                if(!domains.contains(domain)) {
                    domains.add(domain);
                }
            } catch (Exception ignored) {
            }

        }

        for (String domain : domains) {
            checkBlacklist(domain);
            checkSimilar(domain);
            checkBanned(domain);
        }

    }

    /**
     * Auxiliary method to extract domain from a URL
     * @param url The URL
     * @return The domain of the URL
     */
    public String extractDomain(String url) throws Exception {
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
    }

    /**
     * Method to extract domain from URL
     * @param url URL to extract
     * @return URL domain
     * @throws Exception Cannot extract the domain
     */
    public String extractDomainV2(String url) throws Exception {
        String[] split = url.split("//");

        if (split.length == 0) {
            throw new Exception("Not real domain");
        }

        URL uri = new URL(url);

        String check = uri.getHost();
        String substring;

        if (check.length() > 1) {
            substring = check.trim().substring(check.length() - 1);
            if (!substring.equalsIgnoreCase("=")) {
                return check;
            }
        }
        throw new IOException("not domain found");
    }

    /**
     * Method to clear all data
     */
    public void clear() {
        this.banned.clear();
        this.shorten.clear();
        this.blacklist.clear();
        this.shorten_urls.clear();
        this.similar.clear();
    }

    /**
     * Method to get URL correspondences
     * @return URL correspondences
     */
    public ArrayList<String> getShorten_urls() {
        return shorten_urls;
    }

    /**
     * Method to get Banned domains
     * @return Banned domains
     */
    public Map<String, Boolean> getBanned() {
        return banned;
    }

    /**
     * Method to get Blacklisted domains
     * @return Blacklisted domains
     */
    public Map<String, Boolean> getBlacklist() {
        return blacklist;
    }

    /**
     * Method to get URL with shorten service
     * @return Shorten URL
     */
    public Map<String, String> getShorten() {
        return shorten;
    }

    /**
     * Method to get similar correspondences
     * @return Similar correspondences
     */
    public Map<String, String> getSimilar() {
        return similar;
    }
}

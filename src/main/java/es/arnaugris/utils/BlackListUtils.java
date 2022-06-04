package es.arnaugris.utils;

import es.arnaugris.external.BlacklistYaml;
import es.arnaugris.external.DomainYaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BlackListUtils {

    // Singleton Instance
    private static volatile BlackListUtils instance = null;

    // here input you api key from BlacklistMaster
    // www.blacklistmaster.com
    private final String api_key = BlacklistYaml.getInstance().getKey();

    private BlackListUtils() {

    }

    /**
     * Method to get class instance
     * @return The BlackListUtils instance object
     */
    public static BlackListUtils getInstance() {
        // To ensure only one instance is created
        if (instance == null) {
            synchronized (BlackListUtils.class) {
                if (instance == null) {
                    instance = new BlackListUtils();
                }
            }
        }
        return instance;
    }

    /**
     * Method to format the request URL
     * @param domain Domain to check
     * @return The request URL
     */
    private String formatURL(String domain) {
        https://www.blacklistmaster.com/restapi/v1/blacklistcheck/domain/casacam.net?apikey=AzKegv6KxuwTqWJNl9Iahgk0eul6iw14

        return "https://www.blacklistmaster.com/restapi/v1/blacklistcheck/domain/" + domain + "?apikey=" + this.api_key;
    }

    /**
     * Check domain reputation and if it is in a blacklist
     * @param domain The domain to check
     * @return 0 if not danger 1 if domain is dangerous
     * @throws IOException
     */
    public boolean checkDomain(String domain) throws IOException {

        StringBuilder result = new StringBuilder();

        URL url = new URL(formatURL(domain));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(result);

        // TODO
        return true;
    }

    public boolean checkShortener(String url) {
        if (url.contains("goo.gl")) {
            return true;
        }
        if (url.contains("tinyurl.com")) {
            return true;
        }
        if (url.contains("bit.ly")) {
            return true;
        }
        if (url.contains("ow.ly")) {
            return true;
        }
        if (url.contains("is.gd")) {
            return true;
        }
        if (url.contains("buff.ly")) {
            return true;
        }
        if (url.contains("adf.ly")) {
            return true;
        }
        if (url.contains("bit.do")) {
            return true;
        }
        if (url.contains("mcaf.ee")) {
            return true;
        }
        if (url.contains("rebrand.ly")) {
            return true;
        }
        if (url.contains("su.pr")) {
            return true;
        }
        return false;
    }

    public boolean isBanned(String domain) {
        for (String bannedDomain : DomainYaml.getInstance().getBanned()) {
            if (bannedDomain.equalsIgnoreCase(domain)) {
                return true;
            }
        }
        return false;
    }
}

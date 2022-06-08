package es.arnaugris.utils.checks;

import es.arnaugris.external.BlacklistYaml;
import es.arnaugris.external.DomainYaml;

import javax.xml.transform.Result;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

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
        return "https://www.blacklistmaster.com/restapi/v1/blacklistcheck/domain/" + domain + "?apikey=" + this.api_key;
    }

    /**
     * Check domain reputation and if it is in a blacklist
     * @param domain The domain to check
     * @return 0 if not danger 1 if domain is dangerous
     * @throws IOException
     */
    public Map<String, String> checkDomain(String domain) throws IOException {

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

        return convertBlacklistToMap(result.toString());
    }

    public Map<String, String> fakeDomain() throws IOException  {
        String defaultST = "{\"status\":\"Not blacklisted\",\"blacklist_cnt\":1,\"blacklist_severity\":\"\",\"API_calls_remaining\":191,\"response\":\"OK\",\"blacklists\":[]}";
        return convertBlacklistToMap(defaultST);
    }

    private Map<String, String> convertBlacklistToMap(String data) {
        data = data.substring(1, data.length()-1);
        String[] keyValuePairs = data.split(",");
        Map<String,String> map = new HashMap<>();

        for(String pair : keyValuePairs) {
            String[] entry = pair.split(":");
            map.put(entry[0].replaceAll("\"", ""), entry[1].replaceAll("\"", ""));
        }

        return map;
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

    public String getRealURL(String link) {
        //
        URLConnection conn;
        try {
            URL inputURL = new URL(link);
            conn = inputURL.openConnection();
            conn.getHeaderFields();
            return conn.getHeaderField("Location");

        } catch(Exception e) {
            System.out.println("Error: "+ e.getMessage());
        }
        return null;
    }

    public String getRealURLV2(String shorten) throws IOException {
        String shortenToken = "64tNM9WaiGe6EX23cGSI6ZDfqRtqPgJlF866CAcW";
        String destiny = "https://onesimpleapi.com/api/unshorten?token=" + shortenToken + "&url=" + shorten;

        StringBuilder result = new StringBuilder();

        URL url = new URL(destiny);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        } catch (Exception e) {
            return "unreachable";
        }
        return result.toString();
    }

}

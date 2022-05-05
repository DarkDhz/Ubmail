package es.arnaugris.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class BlackListUtils {

    // Singleton Instance
    private static volatile BlackListUtils instance = null;

    // here input you api key fror BlacklistMaster
    // www.blacklistmaster.com
    private String api_key = "7jL0GedmCBjSBNro5Vcjxy3Udffdg660";

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

        System.out.println(result.toString());

        // TODO
        return true;
    }
}

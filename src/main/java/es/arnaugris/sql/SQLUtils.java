package es.arnaugris.sql;

import es.arnaugris.external.SQLYaml;

import java.sql.*;
import java.util.ArrayList;

public class SQLUtils {

    private static volatile SQLUtils instance = null;

    private final ArrayList<String> domains;
    private final ArrayList<String> banned;

    private SQLUtils() {
        domains = new ArrayList<>();
        banned = new ArrayList<>();

    }

    public static SQLUtils getInstance() {
        // To ensure only one instance is created
        if (instance == null) {
            synchronized (SQLUtils.class) {
                if (instance == null) {
                    instance = new SQLUtils();
                }
            }
        }
        return instance;
    }

    /**
     * Method to generate a MySQL connection
     * @return An established connection
     * @throws ClassNotFoundException Can't load driver
     * @throws SQLException SQL db not found
     */
    private Connection generateConnection() throws ClassNotFoundException, SQLException {
        SQLYaml sqlYaml = SQLYaml.getInstance();
        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection("jdbc:mysql://" + sqlYaml.getHost() + ":" + sqlYaml.getPort() + "/" + sqlYaml.getDb(),
                sqlYaml.getUsername(), sqlYaml.getPassword());

    }

    /**
     * Method to load domains data from SQL db
     * @throws SQLException SQL db not found
     * @throws ClassNotFoundException Can't load driver
     */
    public void loadDomains() throws SQLException, ClassNotFoundException {
        Connection con = generateConnection();
        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery("SELECT domain FROM domains;");

        synchronized (SQLUtils.class) {
            this.domains.clear();

            while (rs.next()) {
                this.domains.add(rs.getString(1));
            }
        }

        con.close();

    }

    /**
     * Method to load banned domains data from SQL db
     * @throws SQLException SQL db not found
     * @throws ClassNotFoundException Can't load driver
     */
    public void loadBanned() throws SQLException, ClassNotFoundException {
        Connection con = generateConnection();
        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery("SELECT domain FROM banned;");

        synchronized (SQLUtils.class) {
            this.banned.clear();

            while (rs.next()) {
                this.banned.add(rs.getString(1));
            }
        }

        con.close();

    }

    /**
     * Method to get the list of domains
     * @return List array
     */
    public ArrayList<String> getList() {
        synchronized (SQLUtils.class) {
            return this.domains;
        }

    }

    /**
     * Method to get the list of banned domains
     * @return List array
     */
    public ArrayList<String> getBanned() {
        synchronized (SQLUtils.class) {
            return this.banned;
        }
    }

}

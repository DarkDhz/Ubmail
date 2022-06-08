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

    private Connection generateConnection() throws ClassNotFoundException, SQLException {
        SQLYaml sqlYaml = SQLYaml.getInstance();
        Class.forName("com.mysql.jdbc.Driver");
        String host = "54.36.191.29";
        String user = "root";
        String password = "ubending";
        String database = "ubmail";

        return DriverManager.getConnection("jdbc:mysql://" + sqlYaml.getHost() + ":" + sqlYaml.getPort() + "/" + sqlYaml.getDb(),
                sqlYaml.getUsername(), sqlYaml.getPassword());

    }

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

    public ArrayList<String> getList() {
        synchronized (SQLUtils.class) {
            return this.domains;
        }

    }

    public ArrayList<String> getBanned() {
        synchronized (SQLUtils.class) {
            return this.banned;
        }
    }

}

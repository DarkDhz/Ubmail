package es.arnaugris.sql;

import java.sql.*;
import java.util.ArrayList;

public class SQLUtils {

    private static volatile SQLUtils instance = null;

    private final String host = "54.36.191.29";
    private final String user = "root";
    private final String password = "ubending";
    private final String database = "ubmail";

    private ArrayList<String> domains;
    private ArrayList<String> banned;

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
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + database, user, password);
        return con;

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

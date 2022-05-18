package es.arnaugris.utils;

import java.sql.*;
import java.util.concurrent.locks.ReentrantLock;

public class MySQLUtils {

    private final String ip = "54.36.191.29";
    private final String username = "ubmail";
    private final String password = "ubmail";
    private final int port = 3306;
    private final String database = "ubmail";

    // Singleton Instance
    private static volatile MySQLUtils instance = null;

    private MySQLUtils() throws SQLException {
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://"+ ip + ":" + port + "/" + database, username, password);
        con.close();


    }

    public static MySQLUtils getInstance() throws SQLException {
        // To ensure only one instance is created
        if (instance == null) {
            synchronized (MySQLUtils.class) {
                if (instance == null) {
                    instance = new MySQLUtils();
                }
            }
        }
        return instance;
    }

    public void saveMail(MailData mail) throws SQLException {

        Connection con = DriverManager.getConnection(
                "jdbc:mysql://"+ ip + ":" + port + "/" + database, username, password);
        con.close();

        /*Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery("select * from emp");
        while(rs.next())
            System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));*/

        ReentrantLock lock = new ReentrantLock();

        lock.lock();

        try {
            String sql = "INSERT INTO Mails VALUES (?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, "a1");
            stmt.setString(2, "a2");
            stmt.executeUpdate();

            lock.unlock();

        } catch (Exception ex) {
            lock.unlock();
            throw new SQLException("Unable to process statement");
        }



        con.close();

    }
}

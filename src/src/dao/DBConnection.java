package src.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/college_portal?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "mukesh@1710"; // 🔹 change to your MySQL password

    private static Connection conn;

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("✅ Connected to Database!");
            } catch (SQLException e) {
                System.out.println("❌ Database Connection Failed!");
                e.printStackTrace();
            }
        }
        return conn;
    }
}

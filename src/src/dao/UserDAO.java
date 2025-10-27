package src.dao;

import src.model.User;
import java.sql.*;

public class UserDAO {
    private final String URL = "jdbc:mysql://localhost:3306/college_portal";
    private final String USER = "root";
    private final String PASS = "mukesh@1710";

    public boolean registerUser(User user) {
        String sql = "INSERT INTO Users (name,email,password,role) VALUES (?,?,?,?)";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public User login(String email, String password) {
        String sql = "SELECT * FROM Users WHERE email=? AND password=?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User(rs.getString("name"), email, password, rs.getString("role"));
                u.setUserId(rs.getInt("user_id"));
                return u;
            }

        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean removeStudent(String email) {
        String sql = "DELETE FROM Users WHERE email=? AND role='student'";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}

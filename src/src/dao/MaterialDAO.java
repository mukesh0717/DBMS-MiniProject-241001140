package src.dao;

import src.model.Material;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {
    private final String URL = "jdbc:mysql://localhost:3306/college_portal";
    private final String USER = "root";
    private final String PASS = "mukesh@1710";

    public boolean addMaterial(Material m) {
        String sql = "INSERT INTO Materials (title, subject, uploaded_by, file_path) VALUES (?,?,?,?)";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getSubject());
            ps.setInt(3, m.getUploadedBy());
            ps.setString(4, m.getFilePath());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Material> getAllMaterials() {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT * FROM Materials";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Material m = new Material(
                        rs.getString("title"),
                        rs.getString("subject"),
                        rs.getInt("uploaded_by"),
                        rs.getString("file_path"));
                m.setMaterialId(rs.getInt("material_id"));
                list.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Material getMaterialByTitle(String title) {
        String sql = "SELECT * FROM Materials WHERE title=?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Material m = new Material(
                        rs.getString("title"),
                        rs.getString("subject"),
                        rs.getInt("uploaded_by"),
                        rs.getString("file_path"));
                m.setMaterialId(rs.getInt("material_id"));
                return m;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ NEW: delete material by title
    public boolean deleteMaterialByTitle(String title) {
        String sql = "DELETE FROM Materials WHERE title=?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

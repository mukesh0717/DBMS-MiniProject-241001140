package src.service;

import src.dao.MaterialDAO;
import src.model.Material;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class MaterialService {
    private MaterialDAO materialDAO = new MaterialDAO();
    private final String UPLOAD_DIR = "uploaded_files";
    private final String DOWNLOAD_DIR = "downloads";

    public boolean uploadMaterial(Material m, String originalPath) {
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            File sourceFile = new File(originalPath);
            if (!sourceFile.exists()) return false;

            String destPath = UPLOAD_DIR + File.separator + sourceFile.getName();
            Files.copy(sourceFile.toPath(), new File(destPath).toPath(), StandardCopyOption.REPLACE_EXISTING);

            m.setFilePath(new File(destPath).getAbsolutePath());
            return materialDAO.addMaterial(m);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean downloadMaterial(String title) {
        try {
            Material m = materialDAO.getMaterialByTitle(title);
            if (m == null) return false;

            File sourceFile = new File(m.getFilePath());
            if (!sourceFile.exists()) return false;

            File downloadDir = new File(DOWNLOAD_DIR);
            if (!downloadDir.exists()) downloadDir.mkdirs();

            Files.copy(sourceFile.toPath(),
                    new File(DOWNLOAD_DIR + File.separator + sourceFile.getName()).toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ NEW: delete file from both DB and disk
    public boolean deleteMaterial(String title) {
        try {
            Material m = materialDAO.getMaterialByTitle(title);
            if (m == null) return false;

            File file = new File(m.getFilePath());
            if (file.exists()) file.delete(); // delete from system

            return materialDAO.deleteMaterialByTitle(title); // delete from DB
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Material> getAllMaterials() {
        return materialDAO.getAllMaterials();
    }
}

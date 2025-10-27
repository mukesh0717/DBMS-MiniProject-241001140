package src.model;

public class Material {
    private int materialId;
    private String title;
    private String subject;
    private int uploadedBy;
    private String filePath;

    public Material() {}

    public Material(String title, String subject, int uploadedBy, String filePath) {
        this.title = title;
        this.subject = subject;
        this.uploadedBy = uploadedBy;
        this.filePath = filePath;
    }

    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }

    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public int getUploadedBy() { return uploadedBy; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}

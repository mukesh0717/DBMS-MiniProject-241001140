package src.ui;

import src.model.Material;
import src.model.User;
import src.service.MaterialService;
import src.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Dashboard UI (Swing) — fixed layout so Logout button is always visible.
 */
public class Dashboard extends JFrame {
    private final User user;
    private final MaterialService materialService = new MaterialService();
    private final UserService userService = new UserService();

    private final JTable fileTable;
    private final DefaultTableModel tableModel;

    public Dashboard(User user) {
        this.user = user;
        setTitle("📘 Dashboard - " + user.getName() + " (" + user.getRole() + ")");
        setSize(900, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main layout
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(240, 248, 255));

        // Header
        JLabel header = new JLabel("Welcome, " + user.getName() + " (" + user.getRole() + ")", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(new Color(25, 25, 112));
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        root.add(header, BorderLayout.NORTH);

        // Table (center)
        tableModel = new DefaultTableModel(new String[]{"Title", "Subject"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        fileTable = new JTable(tableModel);
        fileTable.setRowHeight(28);
        fileTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fileTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshFileTable();
        JScrollPane tableScroll = new JScrollPane(fileTable);
        root.add(tableScroll, BorderLayout.CENTER);

        // Right-side button panel with vertical layout
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(220, 0));

        // Top area: action buttons (in a vertical box)
        JPanel actionsBox = new JPanel();
        actionsBox.setLayout(new BoxLayout(actionsBox, BoxLayout.Y_AXIS));
        actionsBox.setOpaque(false);
        actionsBox.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // helper to create consistent buttons
        java.util.function.BiFunction<String, String, JButton> btnFactory = (text, emoji) -> {
            JButton b = new JButton(emoji + " " + text);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setBackground(new Color(70, 130, 180));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            return b;
        };

        // Faculty-only actions
        if (user.getRole().equalsIgnoreCase("faculty")) {
            JButton uploadBtn = btnFactory.apply("Upload File", "⬆️");
            uploadBtn.addActionListener(this::uploadFile);
            actionsBox.add(uploadBtn);
            actionsBox.add(Box.createRigidArea(new Dimension(0, 8)));

            JButton deleteBtn = btnFactory.apply("Delete File", "🗑️");
            deleteBtn.addActionListener(this::deleteFile);
            actionsBox.add(deleteBtn);
            actionsBox.add(Box.createRigidArea(new Dimension(0, 8)));

            JButton addStudentBtn = btnFactory.apply("Add Student", "➕");
            addStudentBtn.addActionListener(this::addStudent);
            actionsBox.add(addStudentBtn);
            actionsBox.add(Box.createRigidArea(new Dimension(0, 8)));

            JButton removeStudentBtn = btnFactory.apply("Remove Student", "❌");
            removeStudentBtn.addActionListener(this::removeStudent);
            actionsBox.add(removeStudentBtn);
            actionsBox.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        // Actions available to all users
        JButton downloadBtn = btnFactory.apply("Download File", "⬇️");
        downloadBtn.addActionListener(this::downloadFile);
        actionsBox.add(downloadBtn);
        actionsBox.add(Box.createRigidArea(new Dimension(0, 12)));

        // Put actionsBox at TOP of rightPanel
        rightPanel.add(actionsBox, BorderLayout.NORTH);

        // Bottom area: Logout button (always visible)
        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setOpaque(false);
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 6, 6, 6));

        JButton logoutBtn = new JButton("🚪 Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.setBackground(new Color(220, 53, 69)); // red
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        logoutBtn.addActionListener(e -> {
            // Confirm then logout
            int confirm = JOptionPane.showConfirmDialog(this, "Do you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // dispose and open login UI on EDT
                SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    new LoginUI();
                });
            }
        });

        // keep logout aligned at bottom center
        logoutPanel.add(logoutBtn, BorderLayout.SOUTH);
        rightPanel.add(logoutPanel, BorderLayout.SOUTH);

        // Add rightPanel to root
        root.add(rightPanel, BorderLayout.EAST);

        // add root to frame
        setContentPane(root);
        setVisible(true);
    }

    // Refresh table contents from service
    private void refreshFileTable() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            try {
                for (Material m : materialService.getAllMaterials()) {
                    tableModel.addRow(new Object[]{m.getTitle(), m.getSubject()});
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading materials: " + ex.getMessage());
            }
        });
    }

    // Action handlers

    private void uploadFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String title = JOptionPane.showInputDialog(this, "Enter display title for the file:", file.getName());
            if (title == null || title.trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Title required."); return; }
            String subject = JOptionPane.showInputDialog(this, "Enter subject:");
            if (subject == null || subject.trim().isEmpty()) subject = "General";

            // call service (assumes uploadMaterial(m, path) exists)
            boolean ok = materialService.uploadMaterial(new Material(title, subject, user.getUserId(), file.getAbsolutePath()), file.getAbsolutePath());
            JOptionPane.showMessageDialog(this, ok ? "✅ File uploaded." : "❌ Upload failed.");
            refreshFileTable();
        }
    }

    private void deleteFile(ActionEvent e) {
        int row = fileTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a file to delete!"); return; }
        String title = fileTable.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Delete '" + title + "' permanently?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = materialService.deleteMaterial(title);
        JOptionPane.showMessageDialog(this, ok ? "✅ File deleted." : "❌ Delete failed.");
        refreshFileTable();
    }

    private void downloadFile(ActionEvent e) {
        int row = fileTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a file to download!"); return; }
        String title = fileTable.getValueAt(row, 0).toString();
        boolean ok = materialService.downloadMaterial(title);
        JOptionPane.showMessageDialog(this, ok ? "✅ File downloaded to downloads/." : "❌ File not found.");
    }

    private void addStudent(ActionEvent e) {
        String name = JOptionPane.showInputDialog(this, "Enter student name:");
        if (name == null || name.trim().isEmpty()) return;
        String email = JOptionPane.showInputDialog(this, "Enter student email:");
        if (email == null || email.trim().isEmpty()) return;
        String pwd = JOptionPane.showInputDialog(this, "Enter password for student:");
        if (pwd == null || pwd.trim().isEmpty()) return;

        boolean ok = userService.registerUser(new User(name, email, pwd, "student"));
        JOptionPane.showMessageDialog(this, ok ? "✅ Student added." : "❌ Failed to add student.");
    }

    private void removeStudent(ActionEvent e) {
        String email = JOptionPane.showInputDialog(this, "Enter student email to remove:");
        if (email == null || email.trim().isEmpty()) return;
        boolean ok = userService.removeStudent(email);
        JOptionPane.showMessageDialog(this, ok ? "✅ Student removed." : "❌ No such student.");
    }
}

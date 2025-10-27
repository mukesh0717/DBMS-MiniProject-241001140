package src.ui;

import src.model.User;
import src.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginUI extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private final UserService userService = new UserService();

    public LoginUI() {
        setTitle("College Portal - Login");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Gradient background panel
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 60, 114), 0, getHeight(), new Color(42, 82, 152));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("College Portal Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(label("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(15);
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(label("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(label("Role:"), gbc);
        gbc.gridx = 1;
        roleBox = new JComboBox<>(new String[]{"student", "faculty"});
        panel.add(roleBox, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(76, 175, 80));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.addActionListener(this::loginAction);
        panel.add(loginBtn, gbc);

        gbc.gridx = 1;
        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(33, 150, 243));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.addActionListener(this::registerAction);
        panel.add(registerBtn, gbc);

        add(panel);
        setVisible(true);
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private void loginAction(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleBox.getSelectedItem();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userService.login(email, password);
        if (user != null && user.getRole().equalsIgnoreCase(role)) {
            JOptionPane.showMessageDialog(this, "✅ Login Successful!");
            dispose();
            new Dashboard(user); // open main dashboard
        } else {
            JOptionPane.showMessageDialog(this, "❌ Invalid credentials or role!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerAction(ActionEvent e) {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JComboBox<String> roleField = new JComboBox<>(new String[]{"student", "faculty"});

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Password:")); panel.add(passField);
        panel.add(new JLabel("Role:")); panel.add(roleField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register New User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String role = (String) roleField.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User(name, email, password, role);
            if (userService.registerUser(user)) {
                JOptionPane.showMessageDialog(this, "✅ Registered successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Registration failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginUI::new);
    }
}

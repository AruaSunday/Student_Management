import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class StudentForm extends JFrame {
    JTextField nameField, regNoField, deptField, levelField;
    JButton addButton, viewButton;

    public StudentForm() {
        setTitle("Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("uniuyo_logo.png").getImage());

        // Gradient background panel
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(26, 26, 38);
                Color color2 = new Color(9, 9, 121);
                Color color3 = new Color(8, 167, 199);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color3);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        gradientPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(15);
        regNoField = new JTextField(15);
        deptField = new JTextField(15);
        levelField = new JTextField(15);

        Color labelColor = Color.WHITE;
        Color fieldBg = new Color(30, 30, 50);
        Color fieldFg = Color.WHITE;
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 13);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(labelColor);
        nameLabel.setFont(labelFont);
        JLabel regNoLabel = new JLabel("Registration No:");
        regNoLabel.setForeground(labelColor);
        regNoLabel.setFont(labelFont);
        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setForeground(labelColor);
        deptLabel.setFont(labelFont);
        JLabel levelLabel = new JLabel("Level:");
        levelLabel.setForeground(labelColor);
        levelLabel.setFont(labelFont);

        nameField.setBackground(fieldBg);
        nameField.setForeground(fieldFg);
        nameField.setCaretColor(fieldFg);
        nameField.setFont(fieldFont);
        regNoField.setBackground(fieldBg);
        regNoField.setForeground(fieldFg);
        regNoField.setCaretColor(fieldFg);
        regNoField.setFont(fieldFont);
        deptField.setBackground(fieldBg);
        deptField.setForeground(fieldFg);
        deptField.setCaretColor(fieldFg);
        deptField.setFont(fieldFont);
        levelField.setBackground(fieldBg);
        levelField.setForeground(fieldFg);
        levelField.setCaretColor(fieldFg);
        levelField.setFont(fieldFont);

        gbc.gridx = 0; gbc.gridy = 0;
        gradientPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        gradientPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gradientPanel.add(regNoLabel, gbc);
        gbc.gridx = 1;
        gradientPanel.add(regNoField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gradientPanel.add(deptLabel, gbc);
        gbc.gridx = 1;
        gradientPanel.add(deptField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gradientPanel.add(levelLabel, gbc);
        gbc.gridx = 1;
        gradientPanel.add(levelField, gbc);

        addButton = new JButton("Add Student");
        viewButton = new JButton("View Students");
        addButton.setBackground(new Color(8, 167, 199));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(labelFont);
        viewButton.setBackground(new Color(9, 9, 121));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFont(labelFont);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gradientPanel.add(buttonPanel, gbc);

        setContentPane(gradientPanel);

        // Add Button Action
        addButton.addActionListener(e -> addStudent());

        // View Button Action
        viewButton.addActionListener(e -> viewStudents());

        setVisible(true);
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String regNo = regNoField.getText().trim();
        String dept = deptField.getText().trim();
        String level = levelField.getText().trim();

        if (name.isEmpty() || regNo.isEmpty() || dept.isEmpty() || level.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.connect()) {
            // Check if reg_no already exists
            String checkSql = "SELECT COUNT(*) FROM StudentsData WHERE reg_no = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, regNo);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Student Already Exists", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
                return;
            }
            rs.close();
            checkStmt.close();

            String sql = "INSERT INTO StudentsData (name, reg_no, department, level) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, regNo);
            stmt.setString(3, dept);
            stmt.setString(4, level);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Student added successfully!");
                nameField.setText("");
                regNoField.setText("");
                deptField.setText("");
                levelField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding student.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewStudents() {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT * FROM StudentsData";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            JTable table = new JTable();
            table.setModel(buildTableModel(rs));
            JOptionPane.showMessageDialog(this, new JScrollPane(table));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static javax.swing.table.TableModel buildTableModel(ResultSet rs) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();

        int columnCount = metaData.getColumnCount();
        java.util.Vector<String> columnNames = new java.util.Vector<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        java.util.Vector<java.util.Vector<Object>> data = new java.util.Vector<>();
        while (rs.next()) {
            java.util.Vector<Object> vector = new java.util.Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                vector.add(rs.getObject(i));
            }
            data.add(vector);
        }

        return new javax.swing.table.DefaultTableModel(data, columnNames);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentForm());
    }
}

package bankmanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;

public class Signup2 extends JFrame implements ActionListener {

        private JTextField textPan, textAadhar, textIncome, textEducation;
        private JComboBox<String> accTypeCombo;
        private JButton nextButton;
        private String formNo;

        public Signup2(String formNo) {
                super("Account Registration - Step 2");
                this.formNo = formNo;

                JPanel mainPanel = new JPanel(new GridBagLayout());
                mainPanel.setBackground(new Color(240, 250, 255));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(8, 8, 8, 8);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                JLabel title = new JLabel("Account Details & Verification", SwingConstants.CENTER);
                title.setFont(new Font("Segoe UI", Font.BOLD, 24));
                title.setForeground(new Color(0, 102, 204));
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                mainPanel.add(title, gbc);
                gbc.gridwidth = 1;

                accTypeCombo = addLabelAndCombo("Account Type:", 1, new String[]{"Savings", "Current"}, mainPanel, gbc);
                textPan = addLabelAndField("PAN Number:", 2, mainPanel, gbc);
                textAadhar = addLabelAndField("Aadhar Number:", 3, mainPanel, gbc);
                textEducation = addLabelAndField("Education:", 4, mainPanel, gbc);
                textIncome = addLabelAndField("Income:", 5, mainPanel, gbc);

                nextButton = new JButton("Next");
                nextButton.setBackground(new Color(0, 102, 204));
                nextButton.setForeground(Color.WHITE);
                nextButton.addActionListener(this);
                gbc.gridx = 1;
                gbc.gridy = 6;
                mainPanel.add(nextButton, gbc);

                add(new JScrollPane(mainPanel));
                setSize(600, 600);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(true);
        }

        private JTextField addLabelAndField(String text, int y, JPanel panel, GridBagConstraints gbc) {
                JLabel label = new JLabel(text);
                gbc.gridx = 0;
                gbc.gridy = y;
                panel.add(label, gbc);
                JTextField tf = new JTextField(20);
                gbc.gridx = 1;
                panel.add(tf, gbc);
                return tf;
        }

        private JComboBox<String> addLabelAndCombo(String text, int y, String[] items, JPanel panel, GridBagConstraints gbc) {
                JLabel label = new JLabel(text);
                gbc.gridx = 0;
                gbc.gridy = y;
                panel.add(label, gbc);
                JComboBox<String> combo = new JComboBox<>(items);
                gbc.gridx = 1;
                panel.add(combo, gbc);
                return combo;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                if (e.getSource() == nextButton) {
                        try {
                                DBConnection c = new DBConnection();
                                String query = "UPDATE SignupDetails SET PAN=?, Aadhar=?, Education=?, Income=?, AccType=? WHERE FormNo=?";
                                PreparedStatement pstmt = c.getConnection().prepareStatement(query); // Corrected: Use getConnection()
                                pstmt.setString(1, textPan.getText());
                                pstmt.setString(2, textAadhar.getText());
                                pstmt.setString(3, textEducation.getText());
                                pstmt.setString(4, textIncome.getText());
                                pstmt.setString(5, (String) accTypeCombo.getSelectedItem());
                                pstmt.setString(6, formNo);
                                pstmt.executeUpdate();

                                setVisible(false);
                                new Signup3(formNo); // Corrected: Pass the formNo
                                pstmt.close();
                                c.close();
                        } catch (Exception ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
                        }
                }
        }
}
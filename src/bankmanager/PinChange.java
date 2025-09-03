package bankmanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;

public class PinChange extends JFrame implements ActionListener {

        private JPasswordField newPinField, confirmPinField;
        private JButton changeButton, backButton;
        private String currentPin;

        public PinChange(String currentPin) {
                this.currentPin = currentPin;

                setTitle("Change PIN");
                setSize(500, 350);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JPanel mainPanel = new JPanel(new GridBagLayout());
                mainPanel.setBackground(new Color(245, 245, 245));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(15, 15, 15, 15);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // Title
                JLabel titleLabel = new JLabel("CHANGE YOUR PIN", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
                titleLabel.setForeground(new Color(0, 102, 204));
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                mainPanel.add(titleLabel, gbc);
                gbc.gridwidth = 1;

                // Labels
                JLabel newPinLabel = new JLabel("New PIN:");
                JLabel confirmPinLabel = new JLabel("Confirm PIN:");
                newPinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                confirmPinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.anchor = GridBagConstraints.LINE_END;
                mainPanel.add(newPinLabel, gbc);

                gbc.gridy = 2;
                mainPanel.add(confirmPinLabel, gbc);

                // Password fields
                newPinField = new JPasswordField(15);
                confirmPinField = new JPasswordField(15);
                newPinField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                confirmPinField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                newPinField.setEchoChar('•');
                confirmPinField.setEchoChar('•');

                gbc.gridx = 1;
                gbc.gridy = 1;
                gbc.anchor = GridBagConstraints.LINE_START;
                mainPanel.add(newPinField, gbc);

                gbc.gridy = 2;
                mainPanel.add(confirmPinField, gbc);

                // Buttons
                changeButton = createButton("CHANGE", new Color(39, 174, 96)); // green
                backButton = createButton("BACK", Color.GRAY); // grey
                Dimension btnSize = new Dimension(150, 40);
                changeButton.setPreferredSize(btnSize);
                backButton.setPreferredSize(btnSize);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
                buttonPanel.setOpaque(false);
                buttonPanel.add(changeButton);
                buttonPanel.add(backButton);

                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.gridwidth = 2;
                gbc.anchor = GridBagConstraints.CENTER;
                mainPanel.add(buttonPanel, gbc);

                add(mainPanel);
                setVisible(true);
        }

        private JButton createButton(String text, Color bgColor) {
                JButton button = new JButton(text);
                button.setFont(new Font("Segoe UI", Font.BOLD, 16));
                button.setForeground(Color.WHITE);
                button.setBackground(bgColor);
                button.setFocusPainted(false);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.addActionListener(this);
                return button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                try {
                        DBConnection connection = new DBConnection();
                        if (e.getSource() == changeButton) {
                                String newPin = new String(newPinField.getPassword());
                                String confirmPin = new String(confirmPinField.getPassword());

                                if (newPin.isEmpty() || confirmPin.isEmpty()) {
                                        JOptionPane.showMessageDialog(this, "Please enter and confirm your new PIN");
                                        return;
                                }

                                if (!newPin.equals(confirmPin)) {
                                        JOptionPane.showMessageDialog(this, "PINs do not match");
                                        return;
                                }

                                if (!newPin.matches("\\d{4,6}")) {
                                        JOptionPane.showMessageDialog(this, "PIN must be 4-6 digits");
                                        return;
                                }

                                if (newPin.equals(currentPin)) {
                                        JOptionPane.showMessageDialog(this, "New PIN cannot be same as current PIN");
                                        return;
                                }

                                String updateBank = "UPDATE AccountDetails SET PIN=? WHERE PIN=?";
                                PreparedStatement pstmt1 = connection.getConnection().prepareStatement(updateBank);
                                pstmt1.setString(1, newPin);
                                pstmt1.setString(2, currentPin);
                                pstmt1.executeUpdate();

                                String updateLogin = "UPDATE LoginDetails SET PIN=? WHERE PIN=?";
                                PreparedStatement pstmt2 = connection.getConnection().prepareStatement(updateLogin);
                                pstmt2.setString(1, newPin);
                                pstmt2.setString(2, currentPin);
                                pstmt2.executeUpdate();

                                JOptionPane.showMessageDialog(this, "PIN changed successfully");

                                setVisible(false);
                                new Main_menu(newPin); // Corrected: Pass the new PIN to Main_menu

                                pstmt1.close();
                                pstmt2.close();
                        } else if (e.getSource() == backButton) {
                                setVisible(false);
                                new Main_menu(currentPin);
                        }
                        connection.close(); // Close connection
                } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error changing PIN: " + ex.getMessage());
                }
        }

        public static void main(String[] args) {
                new PinChange("");
        }
}
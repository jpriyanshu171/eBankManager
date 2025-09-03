package bankmanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Deposit extends JFrame implements ActionListener {

        private String pin;
        private JTextField amountField;
        private JButton depositButton, backButton;

        public Deposit(String pin) {
                super("Deposit Money");
                this.pin = pin;

                // Main panel
                JPanel panel = new JPanel(new GridBagLayout());
                panel.setBackground(new Color(230, 240, 250));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(15, 15, 15, 15);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // Title
                JLabel title = new JLabel("Deposit Money", SwingConstants.CENTER);
                title.setFont(new Font("Segoe UI", Font.BOLD, 26));
                title.setForeground(new Color(0, 51, 102));
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                panel.add(title, gbc);
                gbc.gridwidth = 1;

                // Amount Label
                JLabel amountLabel = new JLabel("Enter Amount (₹):");
                amountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                gbc.gridx = 0;
                gbc.gridy = 1;
                panel.add(amountLabel, gbc);

                // Amount Field
                amountField = new JTextField(15);
                gbc.gridx = 1;
                panel.add(amountField, gbc);

                // Deposit Button
                depositButton = createButton("Deposit", new Color(0, 102, 204));
                gbc.gridx = 0;
                gbc.gridy = 2;
                panel.add(depositButton, gbc);

                // Back Button
                backButton = createButton("Back", Color.GRAY);
                gbc.gridx = 1;
                gbc.gridy = 2;
                panel.add(backButton, gbc);

                add(panel);
                setSize(500, 300);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(true);
        }

        public static void main(String[] args) {
                new Deposit("1234");
        }

        private JButton createButton(String text, Color bgColor) {
                JButton button = new JButton(text);
                button.setFont(new Font("Segoe UI", Font.BOLD, 16));
                button.setForeground(Color.WHITE);
                button.setBackground(bgColor);
                button.setFocusPainted(false);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.addActionListener(this);

                // Hover effect
                button.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseEntered(java.awt.event.MouseEvent evt) {
                                button.setBackground(bgColor.darker());
                        }

                        public void mouseExited(java.awt.event.MouseEvent evt) {
                                button.setBackground(bgColor);
                        }
                });

                return button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                try {
                        DBConnection c = new DBConnection();
                        if (e.getSource() == depositButton) {
                                String amountText = amountField.getText().trim();

                                if (amountText.isEmpty()) {
                                        JOptionPane.showMessageDialog(this, "Please enter amount!");
                                        return;
                                }

                                if (!amountText.matches("^\\d+(\\.\\d{1,2})?$")) {
                                        JOptionPane.showMessageDialog(this, "Invalid amount format!");
                                        return;
                                }

                                double amount = Double.parseDouble(amountText);
                                if (amount <= 0) {
                                        JOptionPane.showMessageDialog(this, "Amount must be greater than 0!");
                                        return;
                                }

                                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                                String query = "INSERT INTO bank VALUES (?, ?, 'Deposit', ?)";
                                PreparedStatement pstmt = c.getConnection().prepareStatement(query);
                                pstmt.setString(1, pin);
                                pstmt.setString(2, date);
                                pstmt.setDouble(3, amount);
                                pstmt.executeUpdate();

                                JOptionPane.showMessageDialog(this, "Deposit Successful! ₹" + amount);
                                setVisible(false);
                                new Main_menu(pin);

                                pstmt.close();
                        } else if (e.getSource() == backButton) {
                                setVisible(false);
                                new Main_menu(pin);
                        }
                        c.close();
                } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
        }
}
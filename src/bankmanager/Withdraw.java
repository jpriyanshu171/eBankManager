package bankmanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Withdraw extends JFrame implements ActionListener {
        private String pin;
        private JTextField amountField;
        private JLabel balanceLabel;
        private JButton withdrawButton, backButton;

        public Withdraw(String pin) {
                super("Withdraw Money");
                this.pin = pin;

                // Main panel
                JPanel panel = new JPanel(new GridBagLayout());
                panel.setBackground(new Color(245, 245, 245));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(15, 15, 15, 15);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // Title
                JLabel title = new JLabel("Withdraw Money", SwingConstants.CENTER);
                title.setFont(new Font("Segoe UI", Font.BOLD, 24));
                title.setForeground(new Color(0, 102, 204));
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                panel.add(title, gbc);
                gbc.gridwidth = 1;

                // Amount label and field
                JLabel amountLabel = new JLabel("Amount to Withdraw:");
                amountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                gbc.gridx = 0;
                gbc.gridy = 1;
                panel.add(amountLabel, gbc);

                amountField = new JTextField(15);
                gbc.gridx = 1;
                panel.add(amountField, gbc);

                // Balance label
                balanceLabel = new JLabel("Current Balance: ₹0.00");
                balanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                gbc.gridx = 0;
                gbc.gridy = 2;
                gbc.gridwidth = 2;
                panel.add(balanceLabel, gbc);
                gbc.gridwidth = 1;

                // Buttons
                withdrawButton = new JButton("Withdraw");
                withdrawButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
                withdrawButton.setBackground(new Color(0, 102, 204));
                withdrawButton.setForeground(Color.WHITE);
                withdrawButton.setFocusPainted(false);
                withdrawButton.addActionListener(this);

                backButton = new JButton("Back");
                backButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
                backButton.setBackground(Color.GRAY);
                backButton.setForeground(Color.WHITE);
                backButton.setFocusPainted(false);
                backButton.addActionListener(this);

                // Set size like Deposit buttons
                Dimension btnSize = new Dimension(150, 40);
                withdrawButton.setPreferredSize(btnSize);
                backButton.setPreferredSize(btnSize);

                JPanel btnPanel = new JPanel();
                btnPanel.setBackground(new Color(245, 245, 245));
                btnPanel.add(withdrawButton);
                btnPanel.add(backButton);

                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.gridwidth = 2;
                panel.add(btnPanel, gbc);

                add(panel);
                setSize(500, 300);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Load current balance
                loadBalance();

                setVisible(true);
        }

        public static void main(String[] args) {
                new Withdraw("1234");
        }

        private void loadBalance() {
                try {
                        DBConnection c = new DBConnection();
                        String query = "SELECT SUM(CASE WHEN type='Deposit' THEN amount ELSE -amount END) AS balance FROM bank WHERE pin=?";
                        PreparedStatement ps = c.getConnection().prepareStatement(query);
                        ps.setString(1, pin);
                        ResultSet rs = ps.executeQuery();
                        double balance = 0;
                        if (rs.next()) balance = rs.getDouble("balance");

                        NumberFormat nf = NumberFormat.getCurrencyInstance();
                        balanceLabel.setText("Current Balance: " + nf.format(balance).replace("$", "₹"));

                        rs.close();
                        ps.close();
                        c.close();
                } catch (Exception e) {
                        e.printStackTrace();
                        balanceLabel.setText("Current Balance: ₹0.00");
                }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                if (e.getSource() == withdrawButton) {
                        String text = amountField.getText().trim();
                        if (text.isEmpty()) {
                                JOptionPane.showMessageDialog(this, "Enter amount to withdraw!");
                                return;
                        }
                        if (!text.matches("\\d+(\\.\\d{1,2})?")) {
                                JOptionPane.showMessageDialog(this, "Invalid amount format!");
                                return;
                        }
                        double amount = Double.parseDouble(text);
                        if (amount <= 0) {
                                JOptionPane.showMessageDialog(this, "Amount must be greater than 0!");
                                return;
                        }

                        try {
                                DBConnection c = new DBConnection();
                                String query = "SELECT SUM(CASE WHEN type='Deposit' THEN amount ELSE -amount END) AS balance FROM bank WHERE pin=?";
                                PreparedStatement ps = c.getConnection().prepareStatement(query);
                                ps.setString(1, pin);
                                ResultSet rs = ps.executeQuery();
                                double balance = 0;
                                if (rs.next()) balance = rs.getDouble("balance");

                                if (amount > balance) {
                                        JOptionPane.showMessageDialog(this, "Insufficient balance!");
                                        return;
                                }

                                String insertQuery = "INSERT INTO bank (pin, date, type, amount) VALUES (?, ?, 'Withdrawal', ?)";
                                ps = c.getConnection().prepareStatement(insertQuery);
                                ps.setString(1, pin);
                                ps.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                                ps.setDouble(3, amount);
                                ps.executeUpdate();

                                JOptionPane.showMessageDialog(this, "₹" + String.format("%.2f", amount) + " withdrawn successfully!");
                                setVisible(false);
                                new Main_menu(pin);

                                ps.close();
                                rs.close();
                                c.close();
                        } catch (Exception ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(this, "Error processing withdrawal!");
                        }

                } else if (e.getSource() == backButton) {
                        setVisible(false);
                        new Main_menu(pin);
                }
        }
}
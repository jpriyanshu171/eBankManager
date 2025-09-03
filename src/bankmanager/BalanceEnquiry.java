package bankmanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BalanceEnquiry extends JFrame implements ActionListener {
        private String pin;
        private JLabel balanceLabel;
        private JLabel lastUpdateLabel;
        private JButton backButton;

        public BalanceEnquiry(String pin) {
                super("Balance Enquiry");
                this.pin = pin;

                // Main panel
                JPanel panel = new JPanel(new GridBagLayout());
                panel.setBackground(new Color(245, 245, 245));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(15, 15, 15, 15);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // Title
                JLabel titleLabel = new JLabel("Account Balance", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                titleLabel.setForeground(new Color(0, 102, 204));
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                panel.add(titleLabel, gbc);
                gbc.gridwidth = 1;

                // Balance Label
                JLabel balanceText = new JLabel("Current Balance:");
                balanceText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                gbc.gridx = 0;
                gbc.gridy = 1;
                panel.add(balanceText, gbc);

                balanceLabel = new JLabel("₹0.00");
                balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                gbc.gridx = 1;
                panel.add(balanceLabel, gbc);

                // Last update
                JLabel lastText = new JLabel("Last Transaction:");
                lastText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                gbc.gridx = 0;
                gbc.gridy = 2;
                panel.add(lastText, gbc);

                lastUpdateLabel = new JLabel("-");
                lastUpdateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                gbc.gridx = 1;
                panel.add(lastUpdateLabel, gbc);

                // Back button
                backButton = new JButton("Back");
                backButton.setBackground(new Color(0, 102, 204));
                backButton.setForeground(Color.WHITE);
                backButton.setFocusPainted(false);
                backButton.addActionListener(this);
                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.gridwidth = 2;
                panel.add(backButton, gbc);

                add(panel);
                setSize(500, 300);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Load balance
                loadBalance();

                setVisible(true);
        }

        private void loadBalance() {
                double balance = 0.0;
                Date lastUpdate = null;
                DBConnection c = null;
                PreparedStatement ps = null;
                ResultSet rs = null;

                try {
                        c = new DBConnection();
                        String query = "SELECT * FROM bank WHERE pin = ? ORDER BY date DESC";
                        ps = c.getConnection().prepareStatement(query);
                        ps.setString(1, pin);
                        rs = ps.executeQuery();

                        while (rs.next()) {
                                double amt = rs.getDouble("amount");
                                String type = rs.getString("type");
                                if ("Deposit".equals(type)) balance += amt;
                                else if ("Withdrawal".equals(type)) balance -= amt;

                                if (lastUpdate == null) {
                                        Timestamp ts = rs.getTimestamp("date");
                                        if (ts != null) lastUpdate = new Date(ts.getTime());
                                }
                        }

                        NumberFormat nf = NumberFormat.getCurrencyInstance();
                        balanceLabel.setText(nf.format(balance).replace("$", "₹"));

                        if (lastUpdate != null) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
                                lastUpdateLabel.setText(sdf.format(lastUpdate));
                        }

                } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error retrieving balance", "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                        try {
                                if (rs != null) rs.close();
                                if (ps != null) ps.close();
                                if (c != null) c.close();
                        } catch (SQLException ex) {
                                ex.printStackTrace();
                        }
                }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                if (e.getSource() == backButton) {
                        setVisible(false);
                        new Main_menu(pin);
                }
        }

        public static void main(String[] args) {
                new BalanceEnquiry("1234");
        }
}

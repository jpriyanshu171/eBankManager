package bankmanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main_menu extends JFrame implements ActionListener {
        private JButton depositBtn, withdrawBtn, pinChangeBtn, balanceBtn, exitBtn;
        private String pin;

        public Main_menu(String pin) {
                this.pin = pin;

                // Frame setup
                setTitle("Banking Main Menu");
                setSize(600, 500);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Main panel
                JPanel mainPanel = new JPanel(new BorderLayout());
                mainPanel.setBackground(new Color(230, 240, 250));
                mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

                // Header
                JLabel titleLabel = new JLabel("Welcome to Your Banking Portal", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
                titleLabel.setForeground(new Color(0, 51, 102));
                mainPanel.add(titleLabel, BorderLayout.NORTH);

                // Menu panel
                JPanel menuPanel = new JPanel(new GridBagLayout());
                menuPanel.setOpaque(false);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(20, 20, 20, 20);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // Buttons
                depositBtn = createMenuButton("DEPOSIT");
                withdrawBtn = createMenuButton("WITHDRAW");
                pinChangeBtn = createMenuButton("PIN CHANGE");
                balanceBtn = createMenuButton("BALANCE ENQUIRY");
                exitBtn = createMenuButton("EXIT");

                // Row 0
                gbc.gridx = 0;
                gbc.gridy = 0;
                menuPanel.add(depositBtn, gbc);

                gbc.gridx = 1;
                menuPanel.add(withdrawBtn, gbc);

                // Row 1
                gbc.gridx = 0;
                gbc.gridy = 1;
                menuPanel.add(pinChangeBtn, gbc);

                gbc.gridx = 1;
                menuPanel.add(balanceBtn, gbc);

                // Row 2 - EXIT button full width
                gbc.gridx = 0;
                gbc.gridy = 2;
                gbc.gridwidth = 2;
                menuPanel.add(exitBtn, gbc);

                mainPanel.add(menuPanel, BorderLayout.CENTER);

                add(mainPanel);
                setVisible(true);
        }

        public static void main(String[] args) {
                new Main_menu("");
        }

        private JButton createMenuButton(String text) {
                JButton button = new JButton(text);
                button.setFont(new Font("Segoe UI", Font.BOLD, 16));
                button.setForeground(Color.WHITE);
                button.setBackground(new Color(0, 102, 204));
                button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
                button.setFocusPainted(false);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));

                // Hover effect
                button.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseEntered(java.awt.event.MouseEvent evt) {
                                button.setBackground(new Color(0, 76, 153));
                        }

                        public void mouseExited(java.awt.event.MouseEvent evt) {
                                button.setBackground(new Color(0, 102, 204));
                        }
                });

                button.addActionListener(this);
                return button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                if (e.getSource() == depositBtn) {
                        new Deposit(pin);
                        setVisible(false);
                } else if (e.getSource() == withdrawBtn) {
                        new Withdraw(pin);
                        setVisible(false);
                } else if (e.getSource() == pinChangeBtn) {
                        new PinChange(pin);
                        setVisible(false);
                } else if (e.getSource() == balanceBtn) {
                        new BalanceEnquiry(pin);
                        setVisible(false);
                } else if (e.getSource() == exitBtn) {
                        int confirm = JOptionPane.showConfirmDialog(
                                this,
                                "Are you sure you want to exit?",
                                "Confirm Exit",
                                JOptionPane.YES_NO_OPTION
                        );
                        if (confirm == JOptionPane.YES_OPTION) {
                                System.exit(0);
                        }
                }
        }
}
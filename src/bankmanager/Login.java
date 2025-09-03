package bankmanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class Login extends JFrame implements ActionListener {

        private JTextField cardField;
        private JPasswordField pinField;
        private JButton loginBtn, clearBtn, signupBtn;

        public Login() {
                super("eBank Manager - Login");

                // Main panel
                JPanel panel = new JPanel(null);
                panel.setBackground(new Color(230, 240, 255));

                // Heading
                JLabel heading = new JLabel("Welcome to eBank Manager", SwingConstants.CENTER);
                heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
                heading.setForeground(new Color(0, 102, 204));
                heading.setBounds(50, 30, 300, 30);
                panel.add(heading);

                // Card No
                JLabel cardLabel = new JLabel("Card Number:");
                cardLabel.setBounds(50, 90, 120, 25);
                panel.add(cardLabel);

                cardField = new JTextField();
                cardField.setBounds(180, 90, 180, 25);
                panel.add(cardField);

                // PIN
                JLabel pinLabel = new JLabel("PIN:");
                pinLabel.setBounds(50, 130, 120, 25);
                panel.add(pinLabel);

                pinField = new JPasswordField();
                pinField.setBounds(180, 130, 180, 25);
                panel.add(pinField);

                // Buttons
                loginBtn = new JButton("Login");
                loginBtn.setBounds(80, 200, 100, 30);
                loginBtn.setBackground(new Color(0, 153, 76));
                loginBtn.setForeground(Color.WHITE);
                loginBtn.addActionListener(this);
                panel.add(loginBtn);

                clearBtn = new JButton("Clear");
                clearBtn.setBounds(200, 200, 100, 30);
                clearBtn.setBackground(new Color(204, 0, 0));
                clearBtn.setForeground(Color.WHITE);
                clearBtn.addActionListener(this);
                panel.add(clearBtn);

                signupBtn = new JButton("Create Account");
                signupBtn.setBounds(100, 250, 180, 30);
                signupBtn.setBackground(new Color(0, 102, 204));
                signupBtn.setForeground(Color.WHITE);
                signupBtn.addActionListener(this);
                panel.add(signupBtn);

                add(panel);
                setSize(450, 350);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                if (e.getSource() == loginBtn) {
                        String cardNo = cardField.getText();
                        String pin = new String(pinField.getPassword());

                        if (cardNo.isEmpty() || pin.isEmpty()) {
                                JOptionPane.showMessageDialog(this, "Enter Card Number & PIN", "Input Error", JOptionPane.WARNING_MESSAGE);
                                return;
                        }

                        try {
                                DBConnection c = new DBConnection();
                                String query = "SELECT * FROM LoginDetails WHERE CardNo = ? AND PIN = ?";
                                PreparedStatement pstmt = c.getConnection().prepareStatement(query);
                                pstmt.setString(1, cardNo);
                                pstmt.setString(2, pin);
                                ResultSet rs = pstmt.executeQuery();

                                if (rs.next()) {
                                        JOptionPane.showMessageDialog(this, "Login Successful!");
                                        setVisible(false);
                                        new Main_menu(pin);
                                } else {
                                        JOptionPane.showMessageDialog(this, "Invalid Card Number or PIN", "Login Failed", JOptionPane.ERROR_MESSAGE);
                                }

                                rs.close();
                                pstmt.close();
                                c.close(); // Correctly closing the connection
                        } catch (Exception ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                        }

                } else if (e.getSource() == clearBtn) {
                        cardField.setText("");
                        pinField.setText("");
                } else if (e.getSource() == signupBtn) {
                        setVisible(false);
                        new Signup();
                }
        }

        public static void main(String[] args) {
                new Login();
        }
}

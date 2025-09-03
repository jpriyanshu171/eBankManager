package bankmanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Signup3 extends JFrame implements ActionListener {

        private JCheckBox atmCardBox, internetBankingBox, mobileBankingBox;
        private JTextField depositField;
        private JButton submitButton;
        private String formNo;

        public Signup3(String formNo) {
                super("Account Registration - Step 3");
                this.formNo = formNo;

                JPanel mainPanel = new JPanel(new GridBagLayout());
                mainPanel.setBackground(new Color(240, 250, 255));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(10, 10, 10, 10);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                JLabel title = new JLabel("Select Services & Deposit", SwingConstants.CENTER);
                title.setFont(new Font("Segoe UI", Font.BOLD, 24));
                title.setForeground(new Color(0, 102, 204));
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                mainPanel.add(title, gbc);
                gbc.gridwidth = 1;

                // Services checkboxes
                atmCardBox = addCheckBox("ATM Card", 1, mainPanel, gbc);
                internetBankingBox = addCheckBox("Internet Banking", 2, mainPanel, gbc);
                mobileBankingBox = addCheckBox("Mobile Banking", 3, mainPanel, gbc);

                // Initial deposit
                JLabel depositLabel = new JLabel("Initial Deposit:");
                gbc.gridx = 0;
                gbc.gridy = 4;
                mainPanel.add(depositLabel, gbc);
                depositField = new JTextField(15);
                gbc.gridx = 1;
                mainPanel.add(depositField, gbc);

                // Submit Button
                submitButton = new JButton("Submit & Generate Card");
                submitButton.setBackground(new Color(0, 102, 204));
                submitButton.setForeground(Color.WHITE);
                submitButton.addActionListener(this);
                gbc.gridx = 1;
                gbc.gridy = 5;
                mainPanel.add(submitButton, gbc);

                add(new JScrollPane(mainPanel));
                setSize(600, 600);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(true);
        }

        public static void main(String[] args) {
                new Signup3("1001");
        }

        private JCheckBox addCheckBox(String text, int y, JPanel panel, GridBagConstraints gbc) {
                JCheckBox box = new JCheckBox(text);
                box.setBackground(new Color(240, 250, 255));
                gbc.gridx = 0;
                gbc.gridy = y;
                gbc.gridwidth = 2;
                panel.add(box, gbc);
                gbc.gridwidth = 1;
                return box;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                if (e.getSource() == submitButton) {
                        String services = "";
                        if (atmCardBox.isSelected()) services += "ATM Card,";
                        if (internetBankingBox.isSelected()) services += "Internet Banking,";
                        if (mobileBankingBox.isSelected()) services += "Mobile Banking,";

                        if (!services.isEmpty()) services = services.substring(0, services.length() - 1);

                        String initialDepositText = depositField.getText();
                        double initialDeposit = 0.0;
                        if (!initialDepositText.isEmpty()) {
                                try {
                                        initialDeposit = Double.parseDouble(initialDepositText);
                                } catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog(this, "Invalid initial deposit amount.");
                                        return;
                                }
                        }

                        // Generate CardNo & PIN
                        Random rnd = new Random();
                        String cardNo = String.valueOf(1000_0000_0000_0000L + (Math.abs(rnd.nextLong()) % 9000_0000_0000_0000L));
                        String pin = String.format("%04d", rnd.nextInt(10000));

                        try {
                                DBConnection c = new DBConnection();

                                // 1. Insert into LoginDetails first (the parent table)
                                String loginQuery = "INSERT INTO LoginDetails (CardNo, PIN, FormNo) VALUES (?,?,?)";
                                PreparedStatement loginPstmt = c.getConnection().prepareStatement(loginQuery);
                                loginPstmt.setString(1, cardNo);
                                loginPstmt.setString(2, pin);
                                loginPstmt.setString(3, formNo);
                                loginPstmt.executeUpdate();

                                // 2. Insert into AccountDetails (the child table)
                                String accQuery = "INSERT INTO AccountDetails (FormNo, AccountType, Services, InitialDeposit, CardNo, PIN) VALUES (?,?,?,?,?,?)";
                                PreparedStatement accPstmt = c.getConnection().prepareStatement(accQuery);
                                accPstmt.setString(1, formNo);
                                accPstmt.setString(2, getAccType(formNo));
                                accPstmt.setString(3, services);
                                accPstmt.setDouble(4, initialDeposit);
                                accPstmt.setString(5, cardNo);
                                accPstmt.setString(6, pin);
                                accPstmt.executeUpdate();

                                // 3. Insert initial deposit into 'bank' table
                                if (initialDeposit > 0) {
                                        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                        String bankQuery = "INSERT INTO bank (pin, date, type, amount) VALUES (?, ?, 'Deposit', ?)";
                                        PreparedStatement bankPstmt = c.getConnection().prepareStatement(bankQuery);
                                        bankPstmt.setString(1, pin);
                                        bankPstmt.setString(2, date);
                                        bankPstmt.setDouble(3, initialDeposit);
                                        bankPstmt.executeUpdate();
                                }

                                JOptionPane.showMessageDialog(this,
                                        "Account Created!\nCard No: " + cardNo + "\nPIN: " + pin,
                                        "Success", JOptionPane.INFORMATION_MESSAGE);

                                setVisible(false);
                                new Main_menu(pin);
                                c.close();
                        } catch (Exception ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(this, "Error during account creation: " + ex.getMessage());
                        }
                }
        }

        // Helper to get account type from SignupDetails
        private String getAccType(String formNo) throws Exception {
                DBConnection c = new DBConnection();
                String query = "SELECT AccType FROM SignupDetails WHERE FormNo=?";
                PreparedStatement pst = c.getConnection().prepareStatement(query);
                pst.setString(1, formNo);
                ResultSet rs = pst.executeQuery();
                String accType = "Savings"; // default fallback
                if (rs.next()) {
                        accType = rs.getString("AccType");
                }
                rs.close();
                pst.close();
                c.close();
                return accType;
        }
}
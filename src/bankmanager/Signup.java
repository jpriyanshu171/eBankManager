package bankmanager;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.util.Random;

public class Signup extends JFrame implements ActionListener {

        private JTextField textName, textFname, textEmail, textAdd, textCity, textState, textPin;
        private JDateChooser dateChooser;
        private JRadioButton maleRadio, femaleRadio, marriedRadio, unmarriedRadio;
        private JComboBox<String> religionCombo, categoryCombo;
        private JButton nextButton;

        public Signup() {
                super("eBank Manager - Account Registration Step 1");

                JPanel mainPanel = new JPanel(new GridBagLayout());
                mainPanel.setBackground(new Color(230, 240, 250));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(8, 8, 8, 8);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // Title
                JLabel title = new JLabel("Account Registration", SwingConstants.CENTER);
                title.setFont(new Font("Segoe UI", Font.BOLD, 24));
                title.setForeground(new Color(0, 102, 204));
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                mainPanel.add(title, gbc);
                gbc.gridwidth = 1;

                // Full Name
                textName = addLabelAndField("Full Name:", 1, mainPanel, gbc);

                // Father's Name
                textFname = addLabelAndField("Father's Name:", 2, mainPanel, gbc);

                // DOB
                addLabel("Date of Birth:", 3, mainPanel, gbc);
                dateChooser = new JDateChooser();
                gbc.gridx = 1;
                mainPanel.add(dateChooser, gbc);

                // Gender
                addLabel("Gender:", 4, mainPanel, gbc);
                maleRadio = new JRadioButton("Male");
                femaleRadio = new JRadioButton("Female");
                ButtonGroup genderGroup = new ButtonGroup();
                genderGroup.add(maleRadio);
                genderGroup.add(femaleRadio);
                JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                genderPanel.setOpaque(false);
                genderPanel.add(maleRadio);
                genderPanel.add(femaleRadio);
                gbc.gridx = 1;
                mainPanel.add(genderPanel, gbc);

                // Email
                textEmail = addLabelAndField("Email:", 5, mainPanel, gbc);

                // Marital Status
                addLabel("Marital Status:", 6, mainPanel, gbc);
                marriedRadio = new JRadioButton("Married");
                unmarriedRadio = new JRadioButton("Unmarried");
                ButtonGroup maritalGroup = new ButtonGroup();
                maritalGroup.add(marriedRadio);
                maritalGroup.add(unmarriedRadio);
                JPanel maritalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                maritalPanel.setOpaque(false);
                maritalPanel.add(marriedRadio);
                maritalPanel.add(unmarriedRadio);
                gbc.gridx = 1;
                mainPanel.add(maritalPanel, gbc);

                // Religion ComboBox
                religionCombo = addLabelAndCombo("Religion:", 7, new String[]{"Hindu", "Muslim", "Sikh", "Christian", "Other"}, mainPanel, gbc);

                // Category ComboBox
                categoryCombo = addLabelAndCombo("Category:", 8, new String[]{"General", "OBC", "SC", "ST", "Other"}, mainPanel, gbc);

                // Address, City, State, Pin
                textAdd = addLabelAndField("Address:", 9, mainPanel, gbc);
                textCity = addLabelAndField("City:", 10, mainPanel, gbc);
                textState = addLabelAndField("State:", 11, mainPanel, gbc);
                textPin = addLabelAndField("Pin Code:", 12, mainPanel, gbc);

                // Next Button
                nextButton = new JButton("Next");
                nextButton.setBackground(new Color(0, 102, 204));
                nextButton.setForeground(Color.WHITE);
                nextButton.addActionListener(this);
                gbc.gridx = 1;
                gbc.gridy = 13;
                mainPanel.add(nextButton, gbc);

                add(new JScrollPane(mainPanel));
                setSize(600, 700);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(true);
        }

        public static void main(String[] args) {
                new Signup();
        }

        private void addLabel(String text, int y, JPanel panel, GridBagConstraints gbc) {
                JLabel label = new JLabel(text);
                gbc.gridx = 0;
                gbc.gridy = y;
                panel.add(label, gbc);
        }

        private JTextField addLabelAndField(String text, int y, JPanel panel, GridBagConstraints gbc) {
                addLabel(text, y, panel, gbc);
                JTextField tf = new JTextField(20);
                gbc.gridx = 1;
                panel.add(tf, gbc);
                return tf;
        }

        private JComboBox<String> addLabelAndCombo(String text, int y, String[] items, JPanel panel, GridBagConstraints gbc) {
                addLabel(text, y, panel, gbc);
                JComboBox<String> combo = new JComboBox<>(items);
                gbc.gridx = 1;
                panel.add(combo, gbc);
                return combo;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                if (e.getSource() == nextButton) {
                        String formNo = "" + Math.abs(new Random().nextLong() % 9000L + 1000L);
                        String name = textName.getText();
                        String fname = textFname.getText();
                        String dob = ((JTextField) dateChooser.getDateEditor().getUiComponent()).getText();
                        String gender = maleRadio.isSelected() ? "Male" : femaleRadio.isSelected() ? "Female" : "";
                        String email = textEmail.getText();
                        String marital = marriedRadio.isSelected() ? "Married" : unmarriedRadio.isSelected() ? "Unmarried" : "";
                        String religion = (String) religionCombo.getSelectedItem();
                        String category = (String) categoryCombo.getSelectedItem();
                        String address = textAdd.getText();
                        String city = textCity.getText();
                        String state = textState.getText();
                        String pin = textPin.getText();

                        try {
                                DBConnection c = new DBConnection();
                                String query = "INSERT INTO SignupDetails (FormNo, Name, FName, DOB, Gender, Email, Martial_Status, Address, City, State, Pincode, Religion, Category) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                                PreparedStatement pstmt = c.getConnection().prepareStatement(query); // Corrected: Use getConnection()
                                pstmt.setString(1, formNo);
                                pstmt.setString(2, name);
                                pstmt.setString(3, fname);
                                pstmt.setString(4, dob);
                                pstmt.setString(5, gender);
                                pstmt.setString(6, email);
                                pstmt.setString(7, marital);
                                pstmt.setString(8, address);
                                pstmt.setString(9, city);
                                pstmt.setString(10, state);
                                pstmt.setString(11, pin);
                                pstmt.setString(12, religion);
                                pstmt.setString(13, category);

                                int result = pstmt.executeUpdate();
                                if (result > 0) {
                                        JOptionPane.showMessageDialog(this, "Step 1 Saved Successfully!");
                                        setVisible(false);
                                        new Signup2(formNo); // Corrected: Pass the generated formNo
                                }
                                pstmt.close();
                                c.close();
                        } catch (Exception ex) {
                                ex.printStackTrace();
                        }
                }
        }
}
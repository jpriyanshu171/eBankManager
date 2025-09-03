package bankmanager;

import javax.swing.*;
import java.sql.*;

public class DBConnection {
        private Connection connection;

        // Constructor: initialize the connection
        public DBConnection() {
                try {
                        // MySQL connection
                        connection = DriverManager.getConnection(
                                "jdbc:mysql://localhost:3306/eBankManager", // your DB name
                                "root", // MySQL username
                                ""      // MySQL password
                        );
                } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Database Connection Failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
        }

        // Get the connection object
        public Connection getConnection() {
                return connection;
        }

        // Prepare statement helper
        public PreparedStatement prepareStatement(String sql) throws SQLException {
                return connection.prepareStatement(sql);
        }

        // Close connection safely
        public void close() {
                try {
                        if (connection != null && !connection.isClosed()) {
                                connection.close();
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }

        // Optional main for testing
        public static void main(String[] args) {
                DBConnection db = new DBConnection();
                if (db.getConnection() != null) {
                        System.out.println("Database Connected Successfully!");
                        db.close();
                }
        }
}
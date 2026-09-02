package cop2805;

/**
 * JDBCreader.java
 * Copyright (c) 2026 Steve Curtis, Six Actual Studios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 * 
 * This class connects to a MySQL database named "cop2805" running on localhost 
 * (via XAMPP) using JDBC. It executes a SELECT * FROM EMPLOYEES query and 
 * prints all records from the Employees table in a formatted table layout.
 * 
 * It uses try-with-resources for automatic resource management and includes
 * proper error handling for connection and query issues.
 * 
 * Although I am accessing the database directly here for the scope of
 * the course, in a production environment I would use a RESTful API to
 * communicate with the database and include an API key or token for
 * security purposes
 */

import java.sql.*;

public class JDBCreader {
	public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/cop2805?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "";

        // SQL query
        String sql = "SELECT * FROM EMPLOYEES";

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establishing connection
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                System.out.println("Connected to MySQL database 'cop2805' successfully!");
                System.out.println("Employees Table Results:");
                System.out.println("-------------------------------------------------------------");

                // Get column metadata so we can print headers dynamically
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Print column headers
                for (int i = 1; i <= columnCount; i++) {
                    System.out.printf("%-15s", metaData.getColumnName(i));
                }
                System.out.println();
                System.out.println("-------------------------------------------------------------");

                // Print each row
                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.printf("%-15s", rs.getString(i));
                    }
                    System.out.println();
                }

                if (rowCount == 0) {
                    System.out.println("No records found in the EMPLOYEES table.");
                } else {
                    System.out.println("-------------------------------------------------------------");
                    System.out.println("Total rows returned: " + rowCount);
                }

            } 

        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found. Make sure mysql-connector-j is in your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

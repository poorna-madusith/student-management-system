package com.example.student_managemnt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:mysql://localhost:3306/stdetails";
            String user = "root";
            String password = "";
            // create a connection to the database
            conn = DriverManager.getConnection(url,user,password);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}

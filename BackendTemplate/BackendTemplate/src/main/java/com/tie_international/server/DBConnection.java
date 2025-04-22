package com.tie_international;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    private String url;
    private String user;
    private String pw;

    private DBConnection() {
        loadConfig();
        establishConnection();
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    private void loadConfig() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("config.properties not found in classpath");
            }
            prop.load(input);
            user = prop.getProperty("user");
            pw = prop.getProperty("pw");
            url = "jdbc:mysql://localhost:3306/" + prop.getProperty("db");
        } catch (IOException e) {
            System.err.println("Failed to load config: " + e.getMessage());
        }
    }

    private void establishConnection() {
        try {
            connection = DriverManager.getConnection(url, user, pw);
            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to establish connection: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean testConnection() {
        try (Connection testConn = DriverManager.getConnection(url, user, pw)) {
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close connection: " + e.getMessage());
        }
    }
}

package com.tie_international.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new IOException("config.properties file not found in the classpath");
                }
                Properties props = new Properties();
                props.load(input);

                String user = props.getProperty("user");
                String password = props.getProperty("pw");
                String db = props.getProperty("db");

                String url = "jdbc:mysql://localhost:3306/" + db;

                // Logging for debugging
                System.out.println("Attempting to connect to DB with URL: " + url);
                System.out.println("Using user: " + user);

                connection = DriverManager.getConnection(url, user, password);
            } catch (IOException e) {
                throw new SQLException("Could not load database configuration", e);
            }
        }
        return connection;
    }
}
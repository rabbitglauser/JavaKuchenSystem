package com.tie_international.DAOs;

import com.tie_international.db.DBConnection;
import com.tie_international.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private final Connection connection;

    public UserDAO() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    public User getUserByUsername(String username) throws SQLException {
        System.out.println("Fetching user with username: " + username);

        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("User found: " + resultSet.getString("username"));
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                );
            } else {
                System.out.println("No user found with username: " + username);
            }
        }
        return null; // User not found
    }

    public User createUser(User user) throws SQLException {
        System.out.println("Creating new user: " + user.getUsername());

        // First check if the username already exists
        if (getUserByUsername(user.getUsername()) != null) {
            throw new SQLException("Username already exists");
        }

        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());

            int rowsInserted = statement.executeUpdate();
            System.out.println("User creation result: " + (rowsInserted > 0 ? "Success" : "Failed"));

            if (rowsInserted > 0) {
                // Get the generated ID
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    return new User(id, user.getUsername(), user.getPassword(), user.getRole());
                }
            }
            return null;
        }
    }

}
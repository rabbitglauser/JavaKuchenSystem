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

    public void createUser(User user) throws SQLException {
        // First check if the username already exists
        User existingUser = getUserByUsername(user.getUsername());
        if (existingUser != null) {
            throw new SQLException("Username already exists");
        }

        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.executeUpdate();
        }
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
        return null;
    }
}
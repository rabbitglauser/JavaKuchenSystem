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
}
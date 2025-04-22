package com.tie_international.server;

import com.tie_international.DAOs.UserDAO;
import com.tie_international.model.User;

import java.sql.SQLException;

public class AuthController {

    private final UserDAO userDAO;

    public AuthController() throws SQLException {
        this.userDAO = new UserDAO();
    }

    public String login(String username, String password) throws SQLException {
        System.out.println("Attempting login for username: " + username);

        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            System.out.println("User not found: " + username);
            throw new SQLException("User not found.");
        }

        System.out.println("User found: " + user.getUsername() + " with role: " + user.getRole());

        if (!user.getPassword().equals(password)) {
            System.out.println("Incorrect password for username: " + username);
            throw new SQLException("Invalid username or password.");
        }

        System.out.println("Login successful for username: " + username);
        return user.getRole();
    }
}
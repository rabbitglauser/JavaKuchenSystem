package com.tie_international.DAOs;

import com.tie_international.db.DBConnection;
import com.tie_international.model.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogDAO {
    public void logAction(String action) throws SQLException {
        String sql = "INSERT INTO logs (action, timestamp) VALUES (?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, action);
            stmt.executeUpdate();
        }
    }
}
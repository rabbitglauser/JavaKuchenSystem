package com.tie_international.DAOs;

import com.tie_international.db.DBConnection;
import com.tie_international.model.Cake;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CakeDAO implements DAO<Cake> {

    private Connection connection;

    public CakeDAO() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Ensures the database connection is valid and reconnects if necessary
     */
    private void ensureConnectionIsValid() throws SQLException {
        if (connection == null || connection.isClosed()) {
            this.connection = DBConnection.getConnection();
        }
    }

    @Override
    public Cake get(int id) throws SQLException {
        ensureConnectionIsValid();
        String query = "SELECT * FROM kuchen WHERE ID_Kuchen = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Cake(
                        rs.getInt("ID_Kuchen"),
                        rs.getString("Kuchen"),
                        rs.getString("Beschreibung"),
                        rs.getInt("DauerInMinuten")
                );
            }
        }
        return null;
    }

    @Override
    public List<Cake> getAll() throws SQLException {
        ensureConnectionIsValid();
        List<Cake> cakes = new ArrayList<>();
        String query = "SELECT * FROM kuchen";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                cakes.add(new Cake(
                        rs.getInt("ID_Kuchen"),
                        rs.getString("Kuchen"),
                        rs.getString("Beschreibung"),
                        rs.getInt("DauerInMinuten")
                ));
            }
        }
        return cakes;
    }

    @Override
    public void save(Cake cake) throws SQLException {
        ensureConnectionIsValid();
        boolean autoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            String query = "INSERT INTO kuchen (Kuchen, Beschreibung, DauerInMinuten) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, cake.getName());
                stmt.setString(2, cake.getDescription());
                stmt.setInt(3, cake.getDurationInMinutes());
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    cake.setId(rs.getInt(1));
                }
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    @Override
    public void update(Cake cake) throws SQLException {
        ensureConnectionIsValid();

        String sql = "UPDATE kuchen SET Kuchen = ?, Beschreibung = ?, DauerInMinuten = ? WHERE ID_Kuchen = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cake.getName());
            stmt.setString(2, cake.getDescription());
            stmt.setInt(3, cake.getDurationInMinutes());
            stmt.setInt(4, cake.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Cake with ID " + cake.getId() + " not found");
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        ensureConnectionIsValid();
        boolean autoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);

            // First check if the cake exists
            if (get(id) == null) {
                throw new SQLException("Cake with ID " + id + " does not exist");
            }

            // Delete related records in kuchen_anlass table first
            String deleteKuchenAnlassQuery = "DELETE FROM kuchen_anlass WHERE Kuchen_ID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteKuchenAnlassQuery)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            // Now delete the cake
            String query = "DELETE FROM kuchen WHERE ID_Kuchen = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Delete failed, no rows affected.");
                }
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    /**
     * Closes the database connection if it's open
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // Log the exception here
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
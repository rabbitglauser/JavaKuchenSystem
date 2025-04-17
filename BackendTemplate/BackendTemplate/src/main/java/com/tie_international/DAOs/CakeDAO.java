package com.tie_international.DAOs;

import com.tie_international.db.DBConnection;
import com.tie_international.model.Cake;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CakeDAO implements DAO<Cake> {

    private final Connection connection;

    public CakeDAO() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public Cake get(int id) throws SQLException {
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
    }

    @Override
    public void update(Cake cake) throws SQLException {
        String query = "UPDATE kuchen SET Kuchen = ?, Beschreibung = ?, DauerInMinuten = ? WHERE ID_Kuchen = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cake.getName());
            stmt.setString(2, cake.getDescription());
            stmt.setInt(3, cake.getDurationInMinutes());
            stmt.setInt(4, cake.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM kuchen WHERE ID_Kuchen = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
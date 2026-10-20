package org.example.dao;

import org.example.model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    // 1. Method to create the table if it doesn't exist
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS vehicles (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " model_name TEXT NOT NULL,\n"
                + " plate_no TEXT NOT NULL,\n"
                + " daily_rate REAL NOT NULL,\n"
                + " status TEXT NOT NULL\n"
                + ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Database error creating table: " + e.getMessage());
        }
    }

    // 2. Method to add a new vehicle
    public void addVehicle(String model, String plate, double rate, String status) {
        String sql = "INSERT INTO vehicles(model_name, plate_no, daily_rate, status) VALUES(?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, model);
            pstmt.setString(2, plate);
            pstmt.setDouble(3, rate);
            pstmt.setString(4, status);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding vehicle: " + e.getMessage());
        }
    }

    // 3. Method to fetch all vehicles and return them as a List
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> fleet = new ArrayList<>();
        String sql = "SELECT id, model_name, plate_no, daily_rate, status FROM vehicles";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Vehicle v = new Vehicle(
                        rs.getInt("id"),
                        rs.getString("model_name"),
                        rs.getString("plate_no"),
                        rs.getDouble("daily_rate"),
                        rs.getString("status")
                );
                fleet.add(v);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching vehicles: " + e.getMessage());
        }

        return fleet; // Return the list (even if it's empty)
    }
}
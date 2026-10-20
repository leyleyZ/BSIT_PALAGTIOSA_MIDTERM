package org.example;

// Import your custom classes
import org.example.dao.DatabaseConnection;
import org.example.model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // 1. Make sure the table exists before we start
        createTable();

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n==========================================================");
            System.out.println("                  CAR RENTAL FLEET TRACKER                ");
            System.out.println("==========================================================");
            System.out.println("[1] Add New Vehicle");
            System.out.println("[2] View Fleet Vehicles");
            System.out.println("[3] Exit System");
            System.out.println("==========================================================");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n--- ADD NEW VEHICLE ---");
                    System.out.print("Enter Model Name: ");
                    String model = scanner.nextLine();

                    System.out.print("Enter License Plate Number: ");
                    String plate = scanner.nextLine();

                    System.out.print("Enter Daily Rate: ");
                    double rate = Double.parseDouble(scanner.nextLine());

                    System.out.print("Enter Status (AVAILABLE / RENTED): ");
                    String status = scanner.nextLine().toUpperCase();

                    addVehicle(model, plate, rate, status);
                    System.out.println("\nSUCCESS: Vehicle registered to fleet!");
                    break;

                case "2":
                    viewVehicles();
                    break;

                case "3":
                    System.out.println("\nExiting system... Thank you!");
                    isRunning = false;
                    break;

                default:
                    System.out.println("\nInvalid choice. Please enter 1, 2, or 3.");
            }
        }
        scanner.close();
    }

    // --- SQL METHODS ---

    private static void createTable() {
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

    private static void addVehicle(String model, String plate, double rate, String status) {
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

    private static void viewVehicles() {
        List<Vehicle> fleet = new ArrayList<>();
        String sql = "SELECT id, model_name, plate_no, daily_rate, status FROM vehicles";

        // Fetch data from database and store in our List of Vehicle records
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
            System.out.println("Error viewing vehicles: " + e.getMessage());
            return; // Stop if there's an error
        }

        // Print the formatted table
        System.out.println("\n----------------------------------------------------------");
        System.out.println("                  RENTAL FLEET VEHICLES                   ");
        System.out.println("----------------------------------------------------------");
        System.out.println("+----+---------------------+--------------+------------+------------+");
        System.out.printf("| %-2s | %-19s | %-12s | %-10s | %-10s |\n",
                "ID", "MODEL NAME", "PLATE NO", "DAILY RATE", "STATUS");
        System.out.println("+----+---------------------+--------------+------------+------------+");

        // Loop through the List to print the vehicles
        for (Vehicle v : fleet) {
            System.out.printf("| %-2d | %-19s | %-12s | %-10.2f | %-10s |\n",
                    v.id(),
                    v.modelName(),
                    v.plateNo(),
                    v.dailyRate(),
                    v.status());
        }

        System.out.println("+----+---------------------+--------------+------------+------------+");
        System.out.println("Total Records Displayed: " + fleet.size());
    }
}
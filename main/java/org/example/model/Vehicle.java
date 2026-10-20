package org.example.model;

public record Vehicle(int id, String modelName, String plateNo, double dailyRate, String status) {

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - Rate: %.2f | Status: %s",
                id, modelName, plateNo, dailyRate, status);
    }
}
package com.example.tracker.model;

import jakarta.persistence.*;

@Entity
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Example: "2025-11"
    private String monthYear;

    private double amount;

    public Budget() {}
    public Budget(String monthYear, double amount) {
        this.monthYear = monthYear;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}

package org.example.cinema.entity;

import java.time.LocalDate;

public class CustomerStat {
    private int id;
    private double totalSpent;
    private int totalTicketsPurchased;
    private int numberOfVisits;
    private LocalDate lastVisitDate;
    private double averageSpendPerVisit;

    public CustomerStat() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public int getTotalTicketsPurchased() {
        return totalTicketsPurchased;
    }

    public void setTotalTicketsPurchased(int totalTicketsPurchased) {
        this.totalTicketsPurchased = totalTicketsPurchased;
    }

    public int getNumberOfVisits() {
        return numberOfVisits;
    }

    public void setNumberOfVisits(int numberOfVisits) {
        this.numberOfVisits = numberOfVisits;
    }

    public LocalDate getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(LocalDate lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public double getAverageSpendPerVisit() {
        return averageSpendPerVisit;
    }

    public void setAverageSpendPerVisit(double averageSpendPerVisit) {
        this.averageSpendPerVisit = averageSpendPerVisit;
    }
}

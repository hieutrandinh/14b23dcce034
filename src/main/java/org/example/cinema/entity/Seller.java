package org.example.cinema.entity;

public class Seller extends Staff {
    private String counterPosition;

    public Seller() {
    }

    public String getCounterPosition() {
        return counterPosition;
    }

    public void setCounterPosition(String counterPosition) {
        this.counterPosition = counterPosition;
    }
}

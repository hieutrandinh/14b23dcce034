package org.example.cinema.entity;

public class CounterPayment extends Payment {
    private String paymentMethod;

    public CounterPayment() {
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}

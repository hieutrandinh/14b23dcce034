package org.example.cinema.entity;

import java.time.LocalDate;

public class Customer extends User {
    private LocalDate dateOfBirth;
    private String address;
    private MemberCard card;

    public Customer() {
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public MemberCard getCard() {
        return card;
    }

    public void setCard(MemberCard card) {
        this.card = card;
    }
}

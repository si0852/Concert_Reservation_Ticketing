package com.hhplus.concert_ticketing.business.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long customerId;

    String userName;
    Double balance;

    public Customer(String userName, Double balance) {
        this.userName = userName;
        this.balance = balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}

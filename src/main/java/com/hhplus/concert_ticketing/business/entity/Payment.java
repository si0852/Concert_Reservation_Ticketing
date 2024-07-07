package com.hhplus.concert_ticketing.business.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long paymentId;

    @NotNull
    Long reservationId;
    @NotNull
    Double amount;
    @NotNull
    LocalDateTime paymentDate;


    public Payment(Long reservationId, Double amount, LocalDateTime paymentDate) {
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }
}

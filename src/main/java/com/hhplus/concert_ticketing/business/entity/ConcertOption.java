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
public class ConcertOption {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long concertOptionId;

    @NotNull
    Long concertId;
    @NotNull
    LocalDateTime concertDate;
    @NotNull
    Double price;

    public ConcertOption(Long concertId, LocalDateTime concertDate, Double price) {
        this.concertId = concertId;
        this.concertDate = concertDate;
        this.price = price;
    }
}

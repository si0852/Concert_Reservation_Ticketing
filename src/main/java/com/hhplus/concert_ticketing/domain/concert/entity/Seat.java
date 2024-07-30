package com.hhplus.concert_ticketing.domain.concert.entity;

import com.hhplus.concert_ticketing.status.SeatStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long seatId;

    @NotNull
    Long concertOptionId;
    @NotBlank
    String seatNumber;
    @NotBlank
    String seatStatus; // 잠김, 열림

    public Seat(Long concertOptionId, String seatNumber, String status) {
        this.concertOptionId = concertOptionId;
        this.seatNumber = seatNumber;
        this.seatStatus = status;
    }

    public void changeStateReserve() {
        this.seatStatus = SeatStatus.RESERVED.toString();
    }

    public void changeStatePaid() {
        this.seatStatus = SeatStatus.PAID.toString();
    }

    public void changeStateUnlock() {
        this.seatStatus = SeatStatus.AVAILABLE.toString();
    }

    public boolean isAvailableSeat() {
        if(this.seatStatus.equals("Lock")) return false;
        return true;
    }
}
package com.hhplus.concert_ticketing.domain.reservation.entity;

import com.hhplus.concert_ticketing.status.ReservationStatus;
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
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long reservationId;

    @NotNull
    Long userId;
    @NotNull
    Long seatId;

    @Version
    Long version;

    String status; // 예약중, 결제됨, 예약취소
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public Reservation(Long userId, Long seatId, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.seatId = seatId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void changeStateCancel() {
        this.status = ReservationStatus.CANCELLED.toString();
    }

    public void changeStateReservation() {
        this.status = ReservationStatus.WAITING.toString();
    }

    public void changeStatePaid() {
        this.status = ReservationStatus.PAID.toString();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

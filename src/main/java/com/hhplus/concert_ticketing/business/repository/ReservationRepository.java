package com.hhplus.concert_ticketing.business.repository;

import com.hhplus.concert_ticketing.business.entity.Reservation;

import java.util.List;

public interface ReservationRepository {

    Reservation saveData(Reservation reservation);

    Reservation update(Reservation reservation);

    List<Reservation> getReservationData(Long userId);

    Reservation getReservationData(Long userId, Long seatId);

    Reservation getReservationDataByReservationId(Long reservationId);

}

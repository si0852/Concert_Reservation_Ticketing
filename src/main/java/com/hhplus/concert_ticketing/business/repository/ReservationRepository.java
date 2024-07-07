package com.hhplus.concert_ticketing.business.repository;

import com.hhplus.concert_ticketing.business.entity.Reservation;

public interface ReservationRepository {

    Reservation saveData(Reservation reservation);

    Reservation update(Reservation reservation);

    Reservation getReservationData(Long userId, Long seatId);
}

package com.hhplus.concert_ticketing.business.service;

import com.hhplus.concert_ticketing.business.entity.Reservation;

public interface ReservationService {

    public Reservation SaveReservationData(Reservation reservation);

    public Reservation getReservationData(Long userId, Long seatId);

    public Reservation UpdateReservationData(Reservation reservation);
}

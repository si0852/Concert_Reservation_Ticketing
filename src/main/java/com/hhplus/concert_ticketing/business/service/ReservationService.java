package com.hhplus.concert_ticketing.business.service;

import com.hhplus.concert_ticketing.business.entity.Reservation;

import java.util.List;

public interface ReservationService {

    public Reservation SaveReservationData(Reservation reservation);

    public Reservation getReservationData(Long userId, Long seatId);

    public List<Reservation> getReservationDataByUserId(Long userId);

    public List<Reservation> getReservationDataByStatus(String status);

    public Reservation getReservationDataByReservationId(Long reservationId);

    public Reservation UpdateReservationData(Reservation reservation);
}

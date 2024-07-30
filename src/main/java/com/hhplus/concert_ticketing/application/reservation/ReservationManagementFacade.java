package com.hhplus.concert_ticketing.application.reservation;

import com.hhplus.concert_ticketing.domain.reservation.entity.Reservation;

public interface ReservationManagementFacade {

    Reservation reservationProgress(String tokenData, Long seatId) throws Exception;
}

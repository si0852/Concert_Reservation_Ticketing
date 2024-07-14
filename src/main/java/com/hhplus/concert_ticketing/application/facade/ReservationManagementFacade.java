package com.hhplus.concert_ticketing.application.facade;

import com.hhplus.concert_ticketing.business.entity.Reservation;

public interface ReservationManagementFacade {

    Reservation reservationProgress(String tokenData, Long seatId);
}

package com.hhplus.concert_ticketing.business.service;

import com.hhplus.concert_ticketing.business.entity.Seat;

public interface SeatService {

    Seat saveSeatData(Seat seat);

    Seat getSeatData(Long concertOptionId);

    Seat updateSeatData(Seat seat);
}

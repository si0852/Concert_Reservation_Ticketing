package com.hhplus.concert_ticketing.business.service;

import com.hhplus.concert_ticketing.business.entity.Seat;

import java.util.List;

public interface SeatService {

    Seat saveSeatData(Seat seat);

    List<Seat> getSeatData(Long concertOptionId, String status);

    Seat updateSeatData(Seat seat);
}

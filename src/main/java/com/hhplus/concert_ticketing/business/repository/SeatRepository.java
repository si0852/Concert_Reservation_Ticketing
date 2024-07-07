package com.hhplus.concert_ticketing.business.repository;

import com.hhplus.concert_ticketing.business.entity.Seat;

public interface SeatRepository {

    Seat saveData(Seat seat);

    Seat getSeatData(Long concertOptionId);

    Seat update(Seat seat);
}

package com.hhplus.concert_ticketing.domain.concert.repository;

import com.hhplus.concert_ticketing.domain.concert.entity.Seat;

import java.util.List;

public interface SeatRepository {

    Seat saveData(Seat seat);

    List<Seat> getSeatData(Long concertOptionId, String status);

    Seat getSeatData(Long seatId);

    Seat update(Seat seat);
}

package com.hhplus.concert_ticketing.business.repository;

import com.hhplus.concert_ticketing.business.entity.Seat;

import java.util.List;

public interface SeatRepository {

    Seat saveData(Seat seat);

    List<Seat> getSeatData(Long concertOptionId, String status);



    Seat update(Seat seat);
}

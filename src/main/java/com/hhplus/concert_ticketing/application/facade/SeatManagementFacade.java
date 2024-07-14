package com.hhplus.concert_ticketing.application.facade;

import com.hhplus.concert_ticketing.business.entity.Seat;

import java.util.List;

public interface SeatManagementFacade {

    List<Seat> getSeatData(Long concertOptionId, String token);
}

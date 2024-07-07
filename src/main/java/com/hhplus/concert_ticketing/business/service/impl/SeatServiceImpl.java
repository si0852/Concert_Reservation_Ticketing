package com.hhplus.concert_ticketing.business.service.impl;

import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.repository.SeatRepository;
import com.hhplus.concert_ticketing.business.service.SeatService;
import org.springframework.stereotype.Service;

@Service
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;

    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public Seat saveSeatData(Seat seat) {
        return seatRepository.saveData(seat);
    }

    @Override
    public Seat getSeatData(Long concertOptionId) {
        return seatRepository.getSeatData(concertOptionId);
    }

    @Override
    public Seat updateSeatData(Seat seat) {
        return seatRepository.update(seat);
    }
}

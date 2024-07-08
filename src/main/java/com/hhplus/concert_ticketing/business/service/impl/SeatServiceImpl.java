package com.hhplus.concert_ticketing.business.service.impl;

import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.repository.SeatRepository;
import com.hhplus.concert_ticketing.business.service.SeatService;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<Seat> getSeatData(Long concertOptionId, String status) {
        return seatRepository.getSeatData(concertOptionId, status);
    }

    @Override
    public Seat updateSeatData(Seat seat) {
        return seatRepository.update(seat);
    }
}

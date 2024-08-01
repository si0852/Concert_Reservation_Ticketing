package com.hhplus.concert_ticketing.infra.concert.impl;

import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import com.hhplus.concert_ticketing.domain.concert.repository.SeatRepository;
import com.hhplus.concert_ticketing.infra.concert.JpaSeatRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeatRepositoryImpl implements SeatRepository {

    private final JpaSeatRepository jpaSeatRepository;

    public SeatRepositoryImpl(JpaSeatRepository jpaSeatRepository) {
        this.jpaSeatRepository = jpaSeatRepository;
    }

    @Override
    public Seat saveData(Seat seat) {
        return jpaSeatRepository.save(seat);
    }

    @Override
    public List<Seat> getSeatData(Long concertOptionId, String status) {
        return jpaSeatRepository.findByConcertOptionIdAndSeatStatus(concertOptionId, status);
    }

    @Override
    public Seat getSeatData(Long seatId) {
        return jpaSeatRepository.findById(seatId).orElse(null);
    }

    @Override
    public Seat update(Seat seat) {
        return jpaSeatRepository.save(seat);
    }
}

package com.hhplus.concert_ticketing.business.repository.impl;

import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.repository.SeatRepository;
import com.hhplus.concert_ticketing.infra.JpaSeatRepository;
import org.springframework.stereotype.Component;

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
    public Seat getSeatData(Long concertOptionId) {
        return jpaSeatRepository.findByConcertOptionId(concertOptionId).orElse(null);
    }

    @Override
    public Seat update(Seat seat) {
        return jpaSeatRepository.save(seat);
    }
}

package com.hhplus.concert_ticketing.business.repository.impl;

import com.hhplus.concert_ticketing.business.entity.Reservation;
import com.hhplus.concert_ticketing.business.repository.ReservationRepository;
import com.hhplus.concert_ticketing.infra.JpaReservationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationRepositoryImpl implements ReservationRepository {

    private final JpaReservationRepository jpaReservationRepository;

    public ReservationRepositoryImpl(JpaReservationRepository jpaReservationRepository) {
        this.jpaReservationRepository = jpaReservationRepository;
    }

    @Override
    public Reservation saveData(Reservation reservation) {
        return jpaReservationRepository.save(reservation);
    }

    @Override
    public Reservation getReservationData(Long userId, Long seatId) {
        return jpaReservationRepository.findByUserIdAndSeatId(userId,seatId).orElse(null);
    }

    @Override
    public Reservation update(Reservation reservation) {
        return jpaReservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> getReservationData(Long userId) {
        return jpaReservationRepository.findAllByUserId(userId);
    }
}

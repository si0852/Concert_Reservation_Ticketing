package com.hhplus.concert_ticketing.business.service.impl;

import com.hhplus.concert_ticketing.business.entity.Reservation;
import com.hhplus.concert_ticketing.business.repository.ReservationRepository;
import com.hhplus.concert_ticketing.business.service.ReservationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Reservation SaveReservationData(Reservation reservation) {
        return reservationRepository.saveData(reservation);
    }

    @Override
    public Reservation getReservationData(Long userId, Long seatId) {
        return reservationRepository.getReservationData(userId, seatId);
    }

    @Override
    public List<Reservation> getReservationDataByUserId(Long userId) {
        return reservationRepository.getReservationData(userId);
    }

    @Override
    public Reservation UpdateReservationData(Reservation reservation) {
        return reservationRepository.update(reservation);
    }
}

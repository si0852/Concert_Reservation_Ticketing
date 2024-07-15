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
        Reservation reservationData = reservationRepository.getReservationData(userId, seatId);
        if(reservationData == null) throw new RuntimeException("예약정보가 없습니다.");
        return reservationData;
    }

    @Override
    public List<Reservation> getReservationDataByUserId(Long userId) {
        List<Reservation> reservationData = reservationRepository.getReservationData(userId);
        if(reservationData.size() == 0) throw new RuntimeException("예약정보가 없습니다.");
        return reservationData;
    }

    @Override
    public List<Reservation> getReservationDataByStatus(String status) {
        List<Reservation> reservationData = reservationRepository.getReservationData(status);
        if(reservationData.size() == 0) throw new RuntimeException("예약정보가 없습니다.");
        return reservationData;
    }

    @Override
    public Reservation getReservationDataByReservationId(Long reservationId) {
        Reservation validationReservationInfo = reservationRepository.getReservationDataByReservationId(reservationId);
        // 예약정보가 없다면
        if(validationReservationInfo == null) throw new RuntimeException("예약정보가 없습니다.");
        return validationReservationInfo;
    }

    @Override
    public Reservation UpdateReservationData(Reservation reservation) {
        return reservationRepository.update(reservation);
    }
}

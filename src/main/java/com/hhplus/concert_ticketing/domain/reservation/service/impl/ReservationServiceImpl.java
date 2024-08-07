package com.hhplus.concert_ticketing.domain.reservation.service.impl;

import com.hhplus.concert_ticketing.domain.reservation.entity.Reservation;
import com.hhplus.concert_ticketing.domain.reservation.repository.ReservationRepository;
import com.hhplus.concert_ticketing.domain.reservation.service.ReservationService;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.util.exception.LockException;
import com.hhplus.concert_ticketing.util.exception.NoInfoException;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    @Override
    public Reservation SaveReservationData(Reservation reservation) {
        return reservationRepository.saveData(reservation);
    }

    @Override
    public Reservation getReservationData(Long userId, Long seatId) {
        Reservation reservationData = reservationRepository.getReservationData(userId, seatId);
        return reservationData;
    }

    @Override
    public List<Reservation> getReservationDataByUserId(Long userId) {
        List<Reservation> reservationData = reservationRepository.getReservationData(userId);
        return reservationData;
    }

    @Override
    public List<Reservation> getReservationDataByStatus(String status) {
        List<Reservation> reservationData = reservationRepository.getReservationData(status);
        if(reservationData.size() == 0) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "예약정보가 없습니다.", null));
        return reservationData;
    }

    @Transactional
    @Override
    public Reservation getReservationDataByReservationId(Long reservationId) {
        try{
            Reservation validationReservationInfo = reservationRepository.getReservationDataByReservationId(reservationId);
            // 예약정보가 없다면
            if(validationReservationInfo == null) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "예약정보가 없습니다.", null));
            return validationReservationInfo;
        } catch (OptimisticLockException e) {
            throw new LockException(new ResponseDto(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null));
        }
    }

    @Transactional
    @Override
    public Reservation UpdateReservationData(Reservation reservation) {
        return reservationRepository.update(reservation);
    }
}

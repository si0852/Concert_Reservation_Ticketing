package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Reservation;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.facade.ScheduleManagementFacade;
import com.hhplus.concert_ticketing.business.service.ReservationService;
import com.hhplus.concert_ticketing.business.service.SeatService;
import com.hhplus.concert_ticketing.business.service.TokenQueueService;
import com.hhplus.concert_ticketing.presentation.dto.response.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduleManagementFacadeImpl implements ScheduleManagementFacade {

    private final TokenQueueService tokenQueueService;
    private final ReservationService reservationService;
    private final SeatService seatService;

    public ScheduleManagementFacadeImpl(TokenQueueService tokenQueueService, ReservationService reservationService, SeatService seatService) {
        this.tokenQueueService = tokenQueueService;
        this.reservationService = reservationService;
        this.seatService = seatService;
    }

    @Transactional
    @Override
    public Boolean expiredToken() {
        try {
            List<Token> tokenListByActive = tokenQueueService.getTokenListByStatus(TokenStatus.ACTIVE.toString());

            for (Token token: tokenListByActive) {
                LocalDateTime createdAt = token.getCreatedAt();
                LocalDateTime expiredAt = token.getExpiresAt();
                Long seconds = Duration.between(createdAt, expiredAt).getSeconds();
                if (seconds > 300L){
                    token.setStatus(TokenStatus.EXPIRED.toString());
                    tokenQueueService.updateToken(token);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    @Override
    public Boolean expiredReservationStatus() {
        try {
            String status = ReservationStatus.WAITING.toString();
            List<Reservation> reservationDataByStatus = reservationService.getReservationDataByStatus(status);

            for (Reservation reservation : reservationDataByStatus) {
                LocalDateTime createdAt = reservation.getCreatedAt();
                LocalDateTime expiredAt = reservation.getUpdatedAt();
                Long seconds = Duration.between(createdAt, expiredAt).getSeconds();
                if (seconds > 300L) {
                    reservation.setStatus(ReservationStatus.CANCELLED.toString());
                    reservationService.UpdateReservationData(reservation);
                    Seat seatData = seatService.getSeatOnlyData(reservation.getSeatId());
                    seatData.setSeatStatus(SeatStatus.AVAILABLE.toString());
                    seatService.updateSeatData(seatData);
                }

            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

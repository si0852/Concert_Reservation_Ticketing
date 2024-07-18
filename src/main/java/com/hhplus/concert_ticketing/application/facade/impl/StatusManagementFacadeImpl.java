package com.hhplus.concert_ticketing.application.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Reservation;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.application.facade.StatusManagementFacade;
import com.hhplus.concert_ticketing.business.service.ConcertService;
import com.hhplus.concert_ticketing.business.service.ReservationService;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.status.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class StatusManagementFacadeImpl implements StatusManagementFacade {

    private final TokenService tokenService;
    private final ReservationService reservationService;
    private final ConcertService concertService;

    public StatusManagementFacadeImpl(TokenService tokenService, ReservationService reservationService, ConcertService concertService) {
        this.tokenService = tokenService;
        this.reservationService = reservationService;
        this.concertService = concertService;
    }

    @Transactional
    @Override
    public Boolean expiredToken() {
        try {
            List<Token> tokenListByActive = tokenService.getTokenListByStatus(TokenStatus.ACTIVE.toString());

            for (Token token: tokenListByActive) {
                LocalDateTime createdAt = token.getCreatedAt();
                LocalDateTime expiredAt = token.getExpiresAt();
                Long seconds = Duration.between(createdAt, expiredAt).getSeconds();
                if (seconds > 300L){
                    token.changeExpired();
                    tokenService.updateToken(token);
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
                    reservation.changeStateCancel();
                    reservationService.UpdateReservationData(reservation);
                    Seat seatData = concertService.getSeatOnlyData(reservation.getSeatId());
                    seatData.changeStateUnlock();
                    concertService.updateSeatData(seatData);
                }

            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

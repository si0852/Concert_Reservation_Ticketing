package com.hhplus.concert_ticketing.business.facade.impl;


import com.hhplus.concert_ticketing.application.facade.ReservationManagementFacade;
import com.hhplus.concert_ticketing.business.entity.Reservation;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.service.ConcertService;
import com.hhplus.concert_ticketing.business.service.ReservationService;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ReservationManagementFacadeImplIntegratedTest {

    private static final Logger log = LoggerFactory.getLogger(ReservationManagementFacadeImplIntegratedTest.class);

    @Autowired
    ReservationManagementFacade reservationManagementFacade;

    @Autowired
    TokenService tokenService;

    @Autowired
    ReservationService reservationService;

    @Autowired
    ConcertService concertService;

    @DisplayName("예약 진행")
    @Test
    void reservation_progress() throws Exception {

        //given
        Token token1 = tokenService.generateToken(1L);
        token1.setStatus(TokenStatus.ACTIVE.toString());
        Token token = tokenService.saveToken(token1);
        Seat seat = concertService.saveSeatData(new Seat(1L, "1A", SeatStatus.AVAILABLE.toString()));

        //when
        Reservation reservation = reservationManagementFacade.reservationProgress(token.getToken(), seat.getSeatId());

        //then
        Reservation reservationDataByReservationId = reservationService.getReservationDataByReservationId(reservation.getReservationId());
        assertThat(reservationDataByReservationId.getReservationId()).isEqualTo(reservation.getReservationId());

    }


}
package com.hhplus.concert_ticketing.business.facade.impl;


import com.hhplus.concert_ticketing.application.facade.ReservationManagementFacade;
import com.hhplus.concert_ticketing.business.entity.Reservation;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.service.ReservationService;
import com.hhplus.concert_ticketing.business.service.SeatService;
import com.hhplus.concert_ticketing.business.service.TokenQueueService;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.presentation.dto.response.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class ReservationManagementFacadeImplIntegratedTest {

    private static final Logger log = LoggerFactory.getLogger(ReservationManagementFacadeImplIntegratedTest.class);

    @Autowired
    ReservationManagementFacade reservationManagementFacade;

    @Autowired
    TokenQueueService tokenQueueService;

    @Autowired
    TokenService tokenService;

    @Autowired
    ReservationService reservationService;

    @Autowired
    SeatService seatService;

    @DisplayName("예약 진행")
    @Test
    void reservation_progress() {

        //given
        Token token1 = tokenService.generateToken(1L);
        token1.setStatus(TokenStatus.ACTIVE.toString());
        Token token = tokenQueueService.saveToken(token1);
        Seat seat = seatService.saveSeatData(new Seat(1L, "1A", SeatStatus.AVAILABLE.toString()));

        //when
        Reservation reservation = reservationManagementFacade.reservationProgress(token.getToken(), seat.getSeatId());

        //then
        Reservation reservationDataByReservationId = reservationService.getReservationDataByReservationId(reservation.getReservationId());
        assertThat(reservationDataByReservationId.getReservationId()).isEqualTo(reservation.getReservationId());

    }


}
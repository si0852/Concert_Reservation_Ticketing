package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.application.facade.ScheduleManagementFacade;
import com.hhplus.concert_ticketing.business.entity.Reservation;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.service.ReservationService;
import com.hhplus.concert_ticketing.business.service.SeatService;
import com.hhplus.concert_ticketing.business.service.TokenQueueService;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.infra.JpaReservationRepository;
import com.hhplus.concert_ticketing.infra.JpaSeatRepository;
import com.hhplus.concert_ticketing.infra.JpaTokenRepository;
import com.hhplus.concert_ticketing.presentation.dto.response.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ScheduleManagementFacadeImplIntegratedTest {


    private static final Logger log = LoggerFactory.getLogger(ScheduleManagementFacadeImplIntegratedTest.class);

    @Autowired
    ScheduleManagementFacade scheduleManagementFacade;

    @Autowired
    TokenQueueService tokenQueueService;

    @Autowired
    ReservationService reservationService;

    @Autowired
    SeatService seatService;

    @Autowired
    TokenService tokenService;

    @Autowired
    JpaTokenRepository jpaTokenRepository;

    @Autowired
    JpaSeatRepository jpaSeatRepository;

    @Autowired
    JpaReservationRepository jpaReservationRepository;

    @BeforeEach
    void set_up() {
        jpaTokenRepository.deleteAll();
        jpaSeatRepository.deleteAll();
        jpaReservationRepository.deleteAll();
    }

    @DisplayName("토큰 만료 처리")
    @Test
    void expired_token() {
        // given
        Token token = tokenService.generateToken(1l);
        token.setStatus(TokenStatus.ACTIVE.toString());
        token.setExpiresAt(token.getCreatedAt().plusMinutes(3));
        tokenQueueService.saveToken(token);

        Token token2 = tokenService.generateToken(2l);
        token2.setStatus(TokenStatus.ACTIVE.toString());
        token2.setExpiresAt(token2.getCreatedAt().plusMinutes(2));
        tokenQueueService.saveToken(token2);

        Token token3 = tokenService.generateToken(3l);
        token3.setStatus(TokenStatus.ACTIVE.toString());
        token3.setExpiresAt(token3.getCreatedAt().plusMinutes(1));
        tokenQueueService.saveToken(token3);

        Token token4 = tokenService.generateToken(4l);
        token4.setStatus(TokenStatus.ACTIVE.toString());
        token4.setExpiresAt(token4.getCreatedAt().plusSeconds(299));
        tokenQueueService.saveToken(token4);

        Token token5 = tokenService.generateToken(5l);
        token5.setStatus(TokenStatus.ACTIVE.toString());
        token5.setExpiresAt(token5.getCreatedAt().plusSeconds(200));
        tokenQueueService.saveToken(token5);

        Token token6 = tokenService.generateToken(6l);
        token6.setStatus(TokenStatus.ACTIVE.toString());
        token6.setExpiresAt(token6.getCreatedAt().plusSeconds(301));
        tokenQueueService.saveToken(token6);

        Token token7 = tokenService.generateToken(7l);
        token7.setStatus(TokenStatus.ACTIVE.toString());
        token7.setExpiresAt(token7.getCreatedAt().plusMinutes(6));
        tokenQueueService.saveToken(token7);

        Token token8 = tokenService.generateToken(8l);
        token8.setStatus(TokenStatus.ACTIVE.toString());
        token8.setExpiresAt(token8.getCreatedAt().plusSeconds(310));
        tokenQueueService.saveToken(token8);

        Token token9 = tokenService.generateToken(9l);
        token9.setStatus(TokenStatus.ACTIVE.toString());
        token9.setExpiresAt(token9.getCreatedAt().plusMinutes(9));
        tokenQueueService.saveToken(token9);

        Token token10 = tokenService.generateToken(10l);
        token10.setStatus(TokenStatus.ACTIVE.toString());
        token10.setExpiresAt(token10.getCreatedAt().plusMinutes(13));
        tokenQueueService.saveToken(token10);

        //when
        scheduleManagementFacade.expiredToken();
        List<Token> tokenListByStatus = tokenQueueService.getTokenListByStatus(TokenStatus.EXPIRED.toString());

        //then
        assertThat(tokenListByStatus.size()).isEqualTo(5);
    }

    @DisplayName("예약 만료 처리")
    @Test
    void reservation_expired() {
        //given
        LocalDateTime now = LocalDateTime.now();
        Seat seat = new Seat(1L, "1A", SeatStatus.RESERVED.toString());
        Seat saveSeatData = seatService.saveSeatData(seat);
        Reservation reservation = new Reservation(1L, saveSeatData.getSeatId(), ReservationStatus.WAITING.toString(), now, now.plusMinutes(4));
        reservationService.SaveReservationData(reservation);

        Seat seat2 = new Seat(1L, "1A", SeatStatus.RESERVED.toString());
        Seat saveSeatData2 = seatService.saveSeatData(seat2);
        Reservation reservation2 = new Reservation(1L, saveSeatData2.getSeatId(), ReservationStatus.WAITING.toString(), now, now.plusMinutes(6));
        reservationService.SaveReservationData(reservation2);

        Seat seat3 = new Seat(1L, "1A", SeatStatus.RESERVED.toString());
        Seat saveSeatData3 = seatService.saveSeatData(seat3);
        Reservation reservation3 = new Reservation(1L, saveSeatData3.getSeatId(), ReservationStatus.WAITING.toString(), now, now.plusMinutes(8));
        reservationService.SaveReservationData(reservation3);

        Seat seat4 = new Seat(1L, "1A", SeatStatus.RESERVED.toString());
        Seat saveSeatData4 = seatService.saveSeatData(seat4);
        Reservation reservation4 = new Reservation(1L, saveSeatData4.getSeatId(), ReservationStatus.WAITING.toString(), now, now.plusMinutes(3));
        reservationService.SaveReservationData(reservation4);

        Seat seat5 = new Seat(1L, "1A", SeatStatus.RESERVED.toString());
        Seat saveSeatData5 = seatService.saveSeatData(seat5);
        Reservation reservation5 = new Reservation(1L, saveSeatData5.getSeatId(), ReservationStatus.WAITING.toString(), now, now.plusMinutes(2));
        reservationService.SaveReservationData(reservation5);

        //when
        scheduleManagementFacade.expiredReservationStatus();
        List<Reservation> cancelRes = reservationService.getReservationDataByStatus(ReservationStatus.CANCELLED.toString());
        Seat seatData = seatService.getSeatOnlyData(seat.getSeatId());
        Seat seatData2 = seatService.getSeatOnlyData(seat2.getSeatId());
        Seat seatData3 = seatService.getSeatOnlyData(seat3.getSeatId());
        Seat seatData4 = seatService.getSeatOnlyData(seat4.getSeatId());
        Seat seatData5 = seatService.getSeatOnlyData(seat5.getSeatId());

        //then
        assertThat(cancelRes.size()).isEqualTo(2);
        assertThat(seatData2.getSeatStatus()).isEqualTo(SeatStatus.AVAILABLE.toString());
        assertThat(seatData3.getSeatStatus()).isEqualTo(SeatStatus.AVAILABLE.toString());

        assertThat(seatData.getSeatStatus()).isEqualTo(SeatStatus.RESERVED.toString());
        assertThat(seatData4.getSeatStatus()).isEqualTo(SeatStatus.RESERVED.toString());
        assertThat(seatData5.getSeatStatus()).isEqualTo(SeatStatus.RESERVED.toString());
    }
}
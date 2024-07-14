package com.hhplus.concert_ticketing.business.facade.impl;


import com.hhplus.concert_ticketing.application.facade.impl.ReservationManagementFacadeImpl;
import com.hhplus.concert_ticketing.business.entity.Reservation;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.service.ReservationService;
import com.hhplus.concert_ticketing.business.service.SeatService;
import com.hhplus.concert_ticketing.business.service.TokenQueueService;
import com.hhplus.concert_ticketing.presentation.dto.response.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationManagementFacadeImplTest {

    private static final Logger log = LoggerFactory.getLogger(ReservationManagementFacadeImplTest.class);

    @InjectMocks
    ReservationManagementFacadeImpl reservationManagementFacade;

    @Mock
    TokenQueueService tokenQueueService;

    @Mock
    ReservationService reservationService;

    @Mock
    SeatService seatService;


    @DisplayName("좌석예약 - 토큰 유효성 체크")
    @Test
    void reservation_token_valid_check() {
        //given
        Long seatId = 1L;
        String tokendata = "Adfasdfs11";
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token token = new Token(userId, "token123123", TokenStatus.WAITING.toString(), now, now.plusMinutes(10));
        when(tokenQueueService.validateTokenByToken(tokendata)).thenReturn(token);

        //when&&then
        assertThrows(RuntimeException.class, () -> {
            reservationManagementFacade.reservationProgress(tokendata, seatId);
        });
    }

    @DisplayName("좌석예약 - 예약정보 확인, 유저가 예약되어 있는 경우")
    @Test
    void reservation_info_valid_check_user() {
        //given
        Long seatId = 1L;
        String tokendata = "Adfasdfs11";
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token token = new Token(userId, "token123123", TokenStatus.ACTIVE.toString(), now, now.plusMinutes(10));
        when(tokenQueueService.validateTokenByToken(tokendata)).thenReturn(token);

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(new Reservation(1L, 1L, ReservationStatus.WAITING.toString(), now, now.plusMinutes(5)));
        reservationList.add(new Reservation(1L, 2L, ReservationStatus.CANCELLED.toString(), now, now.plusMinutes(5)));
        reservationList.add(new Reservation(1L, 3L, ReservationStatus.PAID.toString(), now, now.plusMinutes(5)));
        reservationList.add(new Reservation(1L, 4L, ReservationStatus.WAITING.toString(), now, now.plusMinutes(5)));
        when(reservationService.getReservationDataByUserId(userId)).thenReturn(reservationList);


        //when && then
        assertThrows(RuntimeException.class, () -> {
            reservationManagementFacade.reservationProgress(tokendata, seatId);
        });
    }

    @DisplayName("좌석예약 - 예약정보 확인, 좌석정보가 없을경우 ")
    @Test
    void reservation_info_valid_check() {
        //given
        Long seatId = 1L;
        String tokendata = "Adfasdfs11";
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token token = new Token(userId, "token123123", TokenStatus.ACTIVE.toString(), now, now.plusMinutes(10));
        when(tokenQueueService.validateTokenByToken(tokendata)).thenReturn(token);

        when(reservationService.getReservationDataByUserId(userId)).thenReturn(List.of());

        Reservation reservation = new Reservation(1L, 1L, "", now, now.plusMinutes(5));
        when(reservationService.getReservationData(userId, seatId)).thenReturn(reservation);

        Seat seat = new Seat(1L, "1A", SeatStatus.RESERVED.toString());
        when(seatService.getSeatOnlyData(seatId)).thenReturn(seat);

        //when && then
        assertThrows(RuntimeException.class, () -> {
            reservationManagementFacade.reservationProgress(tokendata, seatId);
        });
    }

}
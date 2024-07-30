package com.hhplus.concert_ticketing.domain.facade.impl;


import com.hhplus.concert_ticketing.application.reservation.impl.ReservationManagementFacadeImpl;
import com.hhplus.concert_ticketing.domain.concert.entity.Concert;
import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.domain.concert.service.ConcertService;
import com.hhplus.concert_ticketing.domain.reservation.entity.Reservation;
import com.hhplus.concert_ticketing.domain.reservation.service.ReservationService;
import com.hhplus.concert_ticketing.domain.queue.service.TokenService;
import com.hhplus.concert_ticketing.status.ReservationStatus;
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
    TokenService tokenService;

    @Mock
    ReservationService reservationService;

    @Mock
    ConcertService concertService;


    @DisplayName("좌석예약 - 토큰 유효성 체크")
    @Test
    void reservation_token_valid_check()  throws Exception{
        //given
        Long seatId = 1L;
        String tokendata = "Adfasdfs11";
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        when(tokenService.validateTokenByToken(tokendata)).thenThrow(RuntimeException.class);

        //when&&then
        assertThrows(RuntimeException.class, () -> {
            reservationManagementFacade.reservationProgress(tokendata, seatId);
        });
    }

    @DisplayName("좌석예약 - 좌석조회시 null 일때")
    @Test
    void reservation_seat_valid_check()  throws Exception{
        //given
        Long seatId = 1L;
        String tokendata = "Adfasdfs11";
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token token = new Token(userId, "token123123", TokenStatus.WAITING.toString(), now, now.plusMinutes(10));
        when(tokenService.validateTokenByToken(tokendata)).thenReturn(token);
        when(concertService.getSeatOnlyData(seatId)).thenThrow(RuntimeException.class);

        //when&&then
        assertThrows(RuntimeException.class, () -> {
            reservationManagementFacade.reservationProgress(tokendata, seatId);
        });
    }

    @DisplayName("좌석예약 - 좌석조회시 상태가 예약상태 일때")
    @Test
    void reservation_seat_valid_check_reserved()  throws Exception{
        //given
        Long seatId = 1L;
        String tokendata = "Adfasdfs11";
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token token = new Token(userId, "token123123", TokenStatus.WAITING.toString(), now, now.plusMinutes(10));
        when(tokenService.validateTokenByToken(tokendata)).thenReturn(token);
        Seat seat = new Seat(1L, "1A", SeatStatus.RESERVED.toString());
        when(concertService.getSeatOnlyData(seatId)).thenReturn(seat);

        //when&&then
        assertThrows(RuntimeException.class, () -> {
            reservationManagementFacade.reservationProgress(tokendata, seatId);
        });
    }

    @DisplayName("좌석예약 - concertOption 조회시 null 일때")
    @Test
    void reservation_concert_option_valid_check_reserved()  throws Exception{
        //given
        Long seatId = 1L;
        String tokendata = "Adfasdfs11";
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token token = new Token(userId, "token123123", TokenStatus.WAITING.toString(), now, now.plusMinutes(10));
        when(tokenService.validateTokenByToken(tokendata)).thenReturn(token);
        Seat seat = new Seat(1L, "1A", SeatStatus.AVAILABLE.toString());
        when(concertService.getSeatOnlyData(seatId)).thenReturn(seat);
        when(concertService.getConcertOptionDataById(seat.getConcertOptionId())).thenThrow(RuntimeException.class);

        //when&&then
        assertThrows(RuntimeException.class, () -> {
            reservationManagementFacade.reservationProgress(tokendata, seatId);
        });
    }

    @DisplayName("좌석예약 - concert 조회시 null 일때")
    @Test
    void reservation_concert_valid_check_reserved()  throws Exception{
        //given
        Long seatId = 1L;
        String tokendata = "Adfasdfs11";
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token token = new Token(userId, "token123123", TokenStatus.WAITING.toString(), now, now.plusMinutes(10));
        when(tokenService.validateTokenByToken(tokendata)).thenReturn(token);
        Seat seat = new Seat(1L, "1A", SeatStatus.AVAILABLE.toString());
        when(concertService.getSeatOnlyData(seatId)).thenReturn(seat);
        ConcertOption concertOption = new ConcertOption(10L, now.plusHours(10), 10000.0);
        when(concertService.getConcertOptionDataById(seat.getConcertOptionId())).thenReturn(concertOption);
        when(concertService.getConcertData(concertOption.getConcertId())).thenThrow(RuntimeException.class);

        //when&&then
        assertThrows(RuntimeException.class, () -> {
            reservationManagementFacade.reservationProgress(tokendata, seatId);
        });
    }

    @DisplayName("좌석예약 - 예약정보 확인, 유저가 예약되어 있는 경우")
    @Test
    void reservation_info_valid_check_user() throws Exception {
        //given
        Long seatId = 1L;
        String tokendata = "Adfasdfs11";
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token token = new Token(userId, "token123123", TokenStatus.ACTIVE.toString(), now, now.plusMinutes(10));
        when(tokenService.validateTokenByToken(tokendata)).thenReturn(token);
        Seat seat = new Seat(1L, "1A", SeatStatus.AVAILABLE.toString());
        when(concertService.getSeatOnlyData(seatId)).thenReturn(seat);
        ConcertOption concertOption = new ConcertOption(10L, now.plusHours(10), 10000.0);
        when(concertService.getConcertOptionDataById(seat.getConcertOptionId())).thenReturn(concertOption);
        Concert concert = new Concert("concert");
        when(concertService.getConcertData(concertOption.getConcertId())).thenReturn(concert);

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


}
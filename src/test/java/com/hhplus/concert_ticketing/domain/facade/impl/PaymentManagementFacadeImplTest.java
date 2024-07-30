package com.hhplus.concert_ticketing.domain.facade.impl;

import com.hhplus.concert_ticketing.application.payment.impl.PaymentManagementFacadeImpl;
import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import com.hhplus.concert_ticketing.domain.concert.service.impl.ConcertServiceImpl;
import com.hhplus.concert_ticketing.domain.payment.service.impl.PaymentServiceImpl;
import com.hhplus.concert_ticketing.domain.point.entity.Customer;
import com.hhplus.concert_ticketing.domain.point.service.impl.CustomerServiceImpl;
import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.domain.queue.service.impl.TokenServiceImpl;
import com.hhplus.concert_ticketing.domain.reservation.entity.Reservation;
import com.hhplus.concert_ticketing.domain.reservation.service.impl.ReservationServiceImpl;
import com.hhplus.concert_ticketing.status.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentManagementFacadeImplTest {

    @InjectMocks
    PaymentManagementFacadeImpl paymentManagementFacade;

    @Mock
    TokenServiceImpl tokenService;


    @Mock
    ReservationServiceImpl reservationService;

    @Mock
    PaymentServiceImpl paymentService;

    @Mock
    CustomerServiceImpl customerService;

    @Mock
    ConcertServiceImpl concertService;


    @DisplayName("Error: 예약정보가 없습니다")
    @Test
    void throw_err_no_reservation() {
        // given
        Long reservationId = 1L;
        String token = "toekn123123asdf";

        when(reservationService.getReservationDataByReservationId(reservationId)).thenReturn(null);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            paymentManagementFacade.paymentProgress(reservationId, token);
        });
    }

    @DisplayName("Error : 좌석정보가 없습니다.")
    @Test
    void throw_err_no_seatInfo() {
        //given
        Long reservationId = 2L;
        String token ="tldisle123";

        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = new Reservation(3L, 4L, ReservationStatus.WAITING.toString(), now, now.plusMinutes(5));
        when(reservationService.getReservationDataByReservationId(reservationId)).thenReturn(reservation);
        when(concertService.getSeatOnlyData(reservation.getSeatId())).thenReturn(null);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            paymentManagementFacade.paymentProgress(reservationId, token);
        });
    }

    @DisplayName("Error : 토큰 정보가 없습니다.")
    @Test
    void throw_err_no_tokenInfo()  throws Exception{
        //given
        Long reservationId = 2L;
        String token ="tldisle123";
        Long concertOptionId = 11L;

        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = new Reservation(3L, 4L, ReservationStatus.WAITING.toString(), now, now.plusMinutes(5));
        when(reservationService.getReservationDataByReservationId(reservationId)).thenReturn(reservation);

        Seat seat = new Seat(concertOptionId, "1A", SeatStatus.AVAILABLE.toString());
        when(concertService.getSeatOnlyData(reservation.getSeatId())).thenReturn(seat);

        when(tokenService.validateTokenByToken(token)).thenReturn(null);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            paymentManagementFacade.paymentProgress(reservationId, token);
        });
    }

    @DisplayName("Error : 토큰 정보가 만료 되었습니다.")
    @Test
    void throw_err_expired_token()  throws Exception{
        //given
        Long reservationId = 2L;
        String token ="tldisle123";
        Long concertOptionId = 11L;

        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = new Reservation(3L, 4L, ReservationStatus.CANCELLED.toString(), now, now.plusMinutes(5));
        when(reservationService.getReservationDataByReservationId(reservationId)).thenReturn(reservation);

        Seat seat = new Seat(concertOptionId, "1A", SeatStatus.AVAILABLE.toString());
        when(concertService.getSeatOnlyData(reservation.getSeatId())).thenReturn(seat);

        Token tokenData = new Token(11L, token, TokenStatus.ACTIVE.toString(), now, now.plusMinutes(5));
        when(tokenService.validateTokenByToken(token)).thenReturn(tokenData);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            paymentManagementFacade.paymentProgress(reservationId, token);
        });
    }

    @DisplayName("예약시간 만료여부 확인: 5분(300초)보다 큰 경우에 대한 테스트")
    @Test
    void checking_reservation_time() {
        //given
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiredAt = createdAt.plusMinutes(6);
        Reservation reservation = new Reservation(3L, 4L, ReservationStatus.WAITING.toString(), createdAt, expiredAt);

        // when
        Long seconds = Duration.between(createdAt, expiredAt).getSeconds();
        String reservationStatus = reservation.getStatus();

        // then
        assertEquals(360L, seconds);
        assertEquals(ReservationStatus.WAITING.toString(), reservationStatus);
        assertEquals(true, (reservationStatus.equals(ReservationStatus.WAITING.toString()) && seconds > 300L));
    }

    @DisplayName("Error : 예약 시간이 만료 되었습니다.")
    @Test
    void throw_err_expired_time()  throws Exception{
        //given
        Long reservationId = 2L;
        String token ="tldisle123";
        Long concertOptionId = 11L;

        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = new Reservation(3L, 4L, ReservationStatus.WAITING.toString(), now, now.plusMinutes(6));
        when(reservationService.getReservationDataByReservationId(reservationId)).thenReturn(reservation);

        Seat seat = new Seat(concertOptionId, "1A", SeatStatus.AVAILABLE.toString());
        when(concertService.getSeatOnlyData(reservation.getSeatId())).thenReturn(seat);

        Token tokenData = new Token(11L, token, TokenStatus.ACTIVE.toString(), now, now.plusMinutes(5));
        when(tokenService.validateTokenByToken(token)).thenReturn(tokenData);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            paymentManagementFacade.paymentProgress(reservationId, token);
        });
    }

    @DisplayName("Error : 콘서트 옵션 데이터가 존재하지 않습니다.")
    @Test
    void throw_err_no_concert_option_data()  throws Exception{
        //given
        Long reservationId = 2L;
        String token ="tldisle123";
        Long concertOptionId = 11L;

        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = new Reservation(3L, 4L, ReservationStatus.WAITING.toString(), now, now.plusMinutes(4));
        when(reservationService.getReservationDataByReservationId(reservationId)).thenReturn(reservation);

        Seat seat = new Seat(concertOptionId, "1A", SeatStatus.AVAILABLE.toString());
        when(concertService.getSeatOnlyData(reservation.getSeatId())).thenReturn(seat);

        Token tokenData = new Token(11L, token, TokenStatus.ACTIVE.toString(), now, now.plusMinutes(4));
        when(tokenService.validateTokenByToken(token)).thenReturn(tokenData);

        when(concertService.getConcertOptionDataById(seat.getConcertOptionId())).thenReturn(null);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            paymentManagementFacade.paymentProgress(reservationId, token);
        });
    }

    @DisplayName("Error : 유저가 존재하지 않습니다.")
    @Test
    void throw_err_no_user()  throws Exception{
        //given
        Long reservationId = 2L;
        String token ="tldisle123";
        Long concertOptionId = 11L;

        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = new Reservation(3L, 4L, ReservationStatus.WAITING.toString(), now, now.plusMinutes(4));
        when(reservationService.getReservationDataByReservationId(reservationId)).thenReturn(reservation);

        Seat seat = new Seat(concertOptionId, "1A", SeatStatus.AVAILABLE.toString());
        when(concertService.getSeatOnlyData(reservation.getSeatId())).thenReturn(seat);

        Token tokenData = new Token(11L, token, TokenStatus.ACTIVE.toString(), now, now.plusMinutes(4));
        when(tokenService.validateTokenByToken(token)).thenReturn(tokenData);

        ConcertOption concertOption = new ConcertOption(seat.getConcertOptionId(), LocalDateTime.now(), 10000.0);
        when(concertService.getConcertOptionDataById(seat.getConcertOptionId())).thenReturn(concertOption);

        when(customerService.getCustomerData(tokenData.getUserId())).thenReturn(null);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            paymentManagementFacade.paymentProgress(reservationId, token);
        });
    }

    @DisplayName("Error : 잔액이 부족합니다.")
    @Test
    void throw_err_insufficient_money()  throws Exception{
        //given
        Long reservationId = 2L;
        String token ="tldisle123";
        Long concertOptionId = 11L;

        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = new Reservation(3L, 4L, ReservationStatus.WAITING.toString(), now, now.plusMinutes(4));
        when(reservationService.getReservationDataByReservationId(reservationId)).thenReturn(reservation);

        Seat seat = new Seat(concertOptionId, "1A", SeatStatus.AVAILABLE.toString());
        when(concertService.getSeatOnlyData(reservation.getSeatId())).thenReturn(seat);

        Token tokenData = new Token(11L, token, TokenStatus.ACTIVE.toString(), now, now.plusMinutes(4));
        when(tokenService.validateTokenByToken(token)).thenReturn(tokenData);

        ConcertOption concertOption = new ConcertOption(seat.getConcertOptionId(), LocalDateTime.now(), 10000.0);
        when(concertService.getConcertOptionDataById(seat.getConcertOptionId())).thenReturn(concertOption);

        Customer customer = new Customer("si", 9000.0);
        when(customerService.getCustomerData(tokenData.getUserId())).thenReturn(customer);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            paymentManagementFacade.paymentProgress(reservationId, token);
        });
    }

}
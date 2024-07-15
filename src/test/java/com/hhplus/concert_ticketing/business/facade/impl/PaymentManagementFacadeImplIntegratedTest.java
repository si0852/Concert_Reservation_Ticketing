package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.application.facade.PaymentManagementFacade;
import com.hhplus.concert_ticketing.application.facade.ReservationManagementFacade;
import com.hhplus.concert_ticketing.business.entity.*;
import com.hhplus.concert_ticketing.business.service.*;
import com.hhplus.concert_ticketing.presentation.dto.response.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PaymentManagementFacadeImplIntegratedTest {

    @Autowired
    PaymentManagementFacade paymentManagementFacade;

    @Autowired
    ReservationManagementFacade reservationManagementFacade;

    @Autowired
    TokenService tokenService;

    @Autowired
    ReservationService reservationService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    CustomerService customerService;

    @Autowired
    ConcertService concertService;


    @DisplayName("결체처리프로세스 통합테스트 - 시간 만료되지 않았을때")
    @Test
    void payment_process_test()  throws Exception{
        //given
        Customer customer = customerService.saveCustomer(new Customer("sihyun", 100000.0));

        Token token1 = tokenService.generateToken(customer.getCustomerId());
        token1.setStatus(TokenStatus.ACTIVE.toString());
        Token token = tokenService.saveToken(token1);

        ConcertOption concertOption = concertService.saveConcertOption(new ConcertOption(1L, LocalDateTime.now(), 10000.0));

        Seat seat = concertService.saveSeatData(new Seat(concertOption.getConcertOptionId(), "1A", SeatStatus.AVAILABLE.toString()));
        Reservation reservation = reservationManagementFacade.reservationProgress(token.getToken(), seat.getSeatId());

        // when
        paymentManagementFacade.paymentProgress(reservation.getReservationId(), token.getToken());
        Token getToken = tokenService.validateTokenByToken(token.getToken());
        Customer getCustomer = customerService.getCustomerData(customer.getCustomerId());
        Seat getSeatData = concertService.getSeatOnlyData(seat.getSeatId());
        Reservation getReservationData = reservationService.getReservationDataByReservationId(reservation.getReservationId());

        //then
        assertThat(getToken.getStatus()).isEqualTo(TokenStatus.EXPIRED.toString());
        assertThat(getCustomer.getBalance()).isEqualTo(90000.0);
        assertThat(getSeatData.getSeatStatus()).isEqualTo(SeatStatus.PAID.toString());
        assertThat(getReservationData.getStatus()).isEqualTo(ReservationStatus.PAID.toString());
    }


    @DisplayName("결체처리프로세스 통합테스트 - 예약 시간 만료되었을떄")
    @Test
    void payment_process_test_expired_time()  throws Exception{
        //given
        Customer customer = customerService.saveCustomer(new Customer("sihyun", 100000.0));

        Token token1 = tokenService.generateToken(customer.getCustomerId());
        token1.setStatus(TokenStatus.ACTIVE.toString());
        Token token = tokenService.saveToken(token1);
        LocalDateTime now = LocalDateTime.now();

        ConcertOption concertOption = concertService.saveConcertOption(new ConcertOption(1L, now, 10000.0));

        Seat seat = concertService.saveSeatData(new Seat(concertOption.getConcertOptionId(), "1A", SeatStatus.AVAILABLE.toString()));
        Reservation reservation = reservationManagementFacade.reservationProgress(token.getToken(), seat.getSeatId());
        reservation.setCreatedAt(now);
        reservation.setUpdatedAt(now.plusMinutes(10));
        reservationService.UpdateReservationData(reservation);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            paymentManagementFacade.paymentProgress(reservation.getReservationId(), token.getToken());
        });
    }
}
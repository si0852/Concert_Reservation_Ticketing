package com.hhplus.concert_ticketing.domain.facade.impl;

import com.hhplus.concert_ticketing.application.facade.PaymentManagementFacade;
import com.hhplus.concert_ticketing.application.facade.ReservationManagementFacade;
import com.hhplus.concert_ticketing.domain.concert.entity.Concert;
import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import com.hhplus.concert_ticketing.domain.concert.service.ConcertService;
import com.hhplus.concert_ticketing.domain.point.entity.Customer;
import com.hhplus.concert_ticketing.domain.payment.service.PaymentService;
import com.hhplus.concert_ticketing.domain.point.service.CustomerService;
import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.domain.queue.service.TokenService;
import com.hhplus.concert_ticketing.domain.reservation.entity.Reservation;
import com.hhplus.concert_ticketing.domain.reservation.service.ReservationService;
import com.hhplus.concert_ticketing.status.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import com.hhplus.concert_ticketing.util.exception.InvalidTokenException;
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
        LocalDateTime now = LocalDateTime.now();
        Customer customer = customerService.saveCustomer(new Customer("sihyun", 100000.0));

        Token token1 = tokenService.generateToken(customer.getCustomerId());
        token1.changeActive();
        Token token = tokenService.saveToken(token1);

        Concert concert = concertService.saveConcertData(new Concert("Concert"));
        ConcertOption concertOption = concertService.saveConcertOption(new ConcertOption(concert.getConcertId(), LocalDateTime.now(), 10000.0));

        Seat seat = concertService.saveSeatData(new Seat(concertOption.getConcertOptionId(), "1A", SeatStatus.AVAILABLE.toString()));
        reservationService.SaveReservationData(new Reservation(customer.getCustomerId(), seat.getSeatId(), "" , now, now.plusMinutes(1)));
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
        token1.changeActive();
        Token token = tokenService.saveToken(token1);
        LocalDateTime now = LocalDateTime.now();

        Concert concert = concertService.saveConcertData(new Concert("Concert"));
        ConcertOption concertOption = concertService.saveConcertOption(new ConcertOption(concert.getConcertId(), now, 10000.0));

        Seat seat = concertService.saveSeatData(new Seat(concertOption.getConcertOptionId(), "1A", SeatStatus.AVAILABLE.toString()));
        reservationService.SaveReservationData(new Reservation(customer.getCustomerId(), seat.getSeatId(), ReservationStatus.WAITING.toString() , now, now.plusMinutes(6)));
        Reservation reservation = reservationManagementFacade.reservationProgress(token.getToken(), seat.getSeatId());
        reservation.setCreatedAt(now);
        reservation.setUpdatedAt(now.plusMinutes(10));
        reservationService.UpdateReservationData(reservation);

        // when && then
        assertThrows(InvalidTokenException.class, () -> {
            paymentManagementFacade.paymentProgress(reservation.getReservationId(), token.getToken());
        });
    }
}
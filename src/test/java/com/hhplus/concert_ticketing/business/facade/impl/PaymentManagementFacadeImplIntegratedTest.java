package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.application.facade.PaymentManagementFacade;
import com.hhplus.concert_ticketing.application.facade.ReservationManagementFacade;
import com.hhplus.concert_ticketing.business.entity.*;
import com.hhplus.concert_ticketing.business.service.*;
import com.hhplus.concert_ticketing.status.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import com.hhplus.concert_ticketing.util.exception.InvalidTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PaymentManagementFacadeImplIntegratedTest {

    private static final Logger log = LoggerFactory.getLogger(PaymentManagementFacadeImplIntegratedTest.class);


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

    @DisplayName("결체처리프로세스 통합테스트 - 결제처리")
    @Test
    void payment_process_success_test() throws Exception{
        //given
        Customer customer = customerService.saveCustomer(new Customer("sihyun", 100000.0));

        Token token1 = tokenService.generateToken(customer.getCustomerId());
        token1.changeActive();
        Token token = tokenService.saveToken(token1);
        LocalDateTime now = LocalDateTime.now();

        Concert concert = concertService.saveConcertData(new Concert("Concert"));
        ConcertOption concertOption = concertService.saveConcertOption(new ConcertOption(concert.getConcertId(), now, 10000.0));

        Seat seat = concertService.saveSeatData(new Seat(concertOption.getConcertOptionId(), "1A", SeatStatus.AVAILABLE.toString()));
        reservationService.SaveReservationData(new Reservation(customer.getCustomerId(), seat.getSeatId(), ReservationStatus.WAITING.toString() , now, now.plusMinutes(4)));
        Reservation reservation = reservationManagementFacade.reservationProgress(token.getToken(), seat.getSeatId());

        //when
        paymentManagementFacade.paymentProgress(reservation.getReservationId(), token.getToken());

        //then
        Token getT = tokenService.validateTokenByToken(token.getToken());
        Reservation rId = reservationService.getReservationDataByReservationId(reservation.getReservationId());
        Customer getCustomer = customerService.getCustomerData(customer.getCustomerId());

        assertThat(getT.getStatus()).isEqualTo(TokenStatus.EXPIRED.toString());
        assertThat(rId.getStatus()).isEqualTo(ReservationStatus.PAID.toString());
        assertThat(getCustomer.getBalance()).isEqualTo(100000.0 - concertOption.getPrice());
    }

    @DisplayName("결체처리프로세스 통합테스트 - 동시성")
    @Test
    void payment_process_concurrent_test() throws Exception{
        //given
        Customer customer = customerService.saveCustomer(new Customer("sihyun", 100000.0));

        Token token1 = tokenService.generateToken(customer.getCustomerId());
        token1.changeActive();
        Token token = tokenService.saveToken(token1);
        LocalDateTime now = LocalDateTime.now();

        Concert concert = concertService.saveConcertData(new Concert("Concert"));
        ConcertOption concertOption = concertService.saveConcertOption(new ConcertOption(concert.getConcertId(), now, 10000.0));

        Seat seat = concertService.saveSeatData(new Seat(concertOption.getConcertOptionId(), "1A", SeatStatus.AVAILABLE.toString()));
        reservationService.SaveReservationData(new Reservation(customer.getCustomerId(), seat.getSeatId(), ReservationStatus.WAITING.toString() , now, now.plusMinutes(4)));
        Reservation reservation = reservationManagementFacade.reservationProgress(token.getToken(), seat.getSeatId());

        //when
        int numberOfThreads = 200;
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        long start = System.nanoTime();
        Runnable task = () -> {
            long startTime = System.nanoTime();
            try {
                paymentManagementFacade.paymentProgress(reservation.getReservationId(), token.getToken());
                Token getT = tokenService.validateTokenByToken(token.getToken());
                log.info("getT status : " + getT.getStatus());
            } catch (Exception e) {
                log.error("Exception in task", e);
            } finally {
                long endTime = System.nanoTime();
                log.info("Execution time: {} ms" , (endTime - startTime) / 1_000_000);
                latch.countDown();
            }
        };

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(task);
        }

        latch.await(); // await() 메서드를 사용하여 모든 스레드가 작업을 마칠 때까지 대기
        executorService.shutdown(); // 스레드 종료
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.nanoTime();
        log.info("Execution time: {} ms", (endTime - start) / 1_000_000);

        //then
        Token getT = tokenService.validateTokenByToken(token.getToken());
        Reservation rId = reservationService.getReservationDataByReservationId(reservation.getReservationId());
        Customer getCustomer = customerService.getCustomerData(customer.getCustomerId());

        assertThat(getT.getStatus()).isEqualTo(TokenStatus.EXPIRED.toString());
        assertThat(rId.getStatus()).isEqualTo(ReservationStatus.PAID.toString());
        assertThat(getCustomer.getBalance()).isEqualTo(100000.0 - concertOption.getPrice());
    }
}
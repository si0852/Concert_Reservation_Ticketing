package com.hhplus.concert_ticketing.domain.facade.impl;


import com.hhplus.concert_ticketing.application.reservation.ReservationManagementFacade;
import com.hhplus.concert_ticketing.domain.concert.entity.Concert;
import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import com.hhplus.concert_ticketing.domain.concert.service.ConcertService;
import com.hhplus.concert_ticketing.domain.point.entity.Customer;
import com.hhplus.concert_ticketing.domain.point.service.CustomerService;
import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.domain.reservation.entity.Reservation;
import com.hhplus.concert_ticketing.domain.reservation.service.ReservationService;
import com.hhplus.concert_ticketing.domain.queue.service.TokenService;
import com.hhplus.concert_ticketing.status.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
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

    @Autowired
    CustomerService customerService;

    @DisplayName("예약 진행")
    @Test
    void reservation_progress() throws Exception {

        //given
        LocalDateTime now = LocalDateTime.now();
        Customer customer = customerService.saveCustomer(new Customer("sihyun", 100000.0));
        Token token1 = tokenService.generateToken(1L);
        token1.changeActive();
        Token token = tokenService.saveToken(token1);

        Concert concert = concertService.saveConcertData(new Concert("Concert"));
        ConcertOption concertOption = concertService.saveConcertOption(new ConcertOption(concert.getConcertId(), LocalDateTime.now(), 10000.0));
        Seat seat = concertService.saveSeatData(new Seat(concertOption.getConcertOptionId(), "1A", SeatStatus.AVAILABLE.toString()));
        reservationService.SaveReservationData(new Reservation(customer.getCustomerId(), seat.getSeatId(), "" , now, now.plusMinutes(1)));

        //when
        Reservation reservation = reservationManagementFacade.reservationProgress(token.getToken(), seat.getSeatId());

        //then
        Reservation reservationDataByReservationId = reservationService.getReservationDataByReservationId(reservation.getReservationId());
        assertThat(reservationDataByReservationId.getReservationId()).isEqualTo(reservation.getReservationId());

    }

    @DisplayName("예약 진행 - 동시성테스트")
    @Test
    void reservation_progress_test() throws Exception {

        //given
        LocalDateTime now = LocalDateTime.now();
        Customer customer = customerService.saveCustomer(new Customer("sihyun", 100000.0));
        Token token1 = tokenService.generateToken(1L);
        token1.changeActive();
        Token token = tokenService.saveToken(token1);

        Concert concert = concertService.saveConcertData(new Concert("Concert"));
        ConcertOption concertOption = concertService.saveConcertOption(new ConcertOption(concert.getConcertId(), LocalDateTime.now(), 10000.0));
        Seat seat = concertService.saveSeatData(new Seat(concertOption.getConcertOptionId(), "1A", SeatStatus.AVAILABLE.toString()));

        //when
        int numberOfThreads = 290;
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        long start = System.nanoTime();
        Runnable task = () -> {
            long startTime = System.nanoTime();
            try {
                Reservation reservation = reservationManagementFacade.reservationProgress(token.getToken(), seat.getSeatId());
                Seat seatOnlyData = concertService.getSeatOnlyData(seat.getSeatId());
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
        Reservation reservationDataByReservationId = reservationService.getReservationData(token.getUserId(), seat.getSeatId());
        assertThat(reservationDataByReservationId.getStatus()).isEqualTo(ReservationStatus.WAITING.toString());

    }


}
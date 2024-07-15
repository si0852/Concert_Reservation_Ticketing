package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Reservation;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.presentation.dto.response.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StatusManagementFacadeImplTest {


    private static final Logger log = LoggerFactory.getLogger(StatusManagementFacadeImplTest.class);

    @DisplayName("토큰 만료: Active 상태인 토큰 데이터를 여러개 주어졌을때 5분이 넘었을 경우 상태값이 Active로 변경되는지 테스트")
    @Test
    void expired_token_test() {

        //given
        LocalDateTime now = LocalDateTime.now();
        List<Token> tokenListByActive = List.of(
                new Token(1L, "adfasdf", TokenStatus.ACTIVE.toString(), now.plusMinutes(5), now.plusMinutes(9)),
                new Token(2L, "sdfasdf", TokenStatus.ACTIVE.toString(), now, now.plusMinutes(6)),
                new Token(3L, "xcbxcvx", TokenStatus.ACTIVE.toString(), now, now.plusMinutes(7)),
                new Token(4L, "bdbdrdr", TokenStatus.ACTIVE.toString(), now.plusMinutes(10), now.plusMinutes(12)),
                new Token(5L, "ghjkhgj", TokenStatus.ACTIVE.toString(), now, now.plusSeconds(100)),
                new Token(6L, "qwerqez", TokenStatus.ACTIVE.toString(), now, now.plusSeconds(299)),
                new Token(7L, "bvcvfgg", TokenStatus.ACTIVE.toString(), now, now.plusSeconds(301)),
                new Token(8L, "bvcvfgg", TokenStatus.ACTIVE.toString(), now, now.plusSeconds(331))
        );

        List<Token> tokenLisByExpired = new ArrayList<>();

        //when
        for (Token token: tokenListByActive ) {
            LocalDateTime createdAt = token.getCreatedAt();
            LocalDateTime expiredAt = token.getExpiresAt();
            Long seconds = Duration.between(createdAt, expiredAt).getSeconds();
            if (seconds > 300L){
                token.setStatus(TokenStatus.EXPIRED.toString());
                log.info("tokenInfo : " + token);
                tokenLisByExpired.add(token);
            }
        }

        //then
        assertThat(tokenLisByExpired.size()).isEqualTo(4);
    }

    @DisplayName("스케줄러: Waiting인 예약 데이터가 주어졌을때 5분이 넘었을 경우 예약과 좌석 데이터 상태가 각각 취소, unlock처리가 되는지 테스트")
    @Test
    void expired_reservation_and_unlock_seat() {
        //given
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> reservations = List.of(
                new Reservation(1L, 1L, ReservationStatus.WAITING.toString(), now, now.plusSeconds(299)),
                new Reservation(2L, 2L, ReservationStatus.WAITING.toString(), now, now.plusSeconds(302)),
                new Reservation(3L, 3L, ReservationStatus.WAITING.toString(), now, now.plusSeconds(500)),
                new Reservation(4L, 4L, ReservationStatus.WAITING.toString(), now, now.plusSeconds(1)),
                new Reservation(5L, 5L, ReservationStatus.WAITING.toString(), now, now.plusMinutes(5)),
                new Reservation(6L, 6L, ReservationStatus.WAITING.toString(), now, now.plusMinutes(6)),
                new Reservation(7L, 7L, ReservationStatus.WAITING.toString(), now, now.plusMinutes(3)),
                new Reservation(8L, 8L, ReservationStatus.WAITING.toString(), now, now.plusMinutes(10)),
                new Reservation(9L, 9L, ReservationStatus.WAITING.toString(), now, now.plusSeconds(220)),
                new Reservation(10L, 10L, ReservationStatus.WAITING.toString(), now, now.plusSeconds(499)),
                new Reservation(11L, 11L, ReservationStatus.WAITING.toString(), now, now.plusSeconds(699))
        );

        List<Seat> seats = List.of(
                new Seat(1L, "1A", SeatStatus.RESERVED.toString()),
                new Seat(2L, "2A", SeatStatus.RESERVED.toString()),
                new Seat(3L, "3A", SeatStatus.RESERVED.toString()),
                new Seat(4L, "4A", SeatStatus.RESERVED.toString()),
                new Seat(5L, "5A", SeatStatus.RESERVED.toString()),
                new Seat(6L, "6A", SeatStatus.RESERVED.toString()),
                new Seat(7L, "7A", SeatStatus.RESERVED.toString()),
                new Seat(8L, "8A", SeatStatus.RESERVED.toString()),
                new Seat(9L, "9A", SeatStatus.RESERVED.toString()),
                new Seat(10L, "10A", SeatStatus.RESERVED.toString()),
                new Seat(11L, "11A", SeatStatus.RESERVED.toString())
        );

        List<Reservation> reservationList = new ArrayList<>();
        List<Seat> seatList = new ArrayList<>();
        for (Reservation reservation : reservations) {
            LocalDateTime createdAt = reservation.getCreatedAt();
            LocalDateTime expiredAt = reservation.getUpdatedAt();
            Long seconds = Duration.between(createdAt, expiredAt).getSeconds();
            if (seconds > 300L) {
                reservation.setStatus(ReservationStatus.CANCELLED.toString());
                log.info("reservation : " +reservation);
                reservationList.add(reservation);
                Seat seatData = seats.get(Math.toIntExact(reservation.getSeatId())-1);
                log.info("seatData : " +seatData);
                seatData.setSeatStatus(SeatStatus.AVAILABLE.toString());
                seatList.add(seatData);
            }
        }

        assertThat(reservationList.size()).isEqualTo(6);
        assertThat(seatList.size()).isEqualTo(6);
    }
}
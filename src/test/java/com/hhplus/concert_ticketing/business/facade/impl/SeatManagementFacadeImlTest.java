package com.hhplus.concert_ticketing.business.facade.impl;


import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.service.ConcertOptionService;
import com.hhplus.concert_ticketing.business.service.TokenQueueService;
import com.hhplus.concert_ticketing.business.service.impl.ConcertOptionServiceImpl;
import com.hhplus.concert_ticketing.business.service.impl.SeatServiceImpl;
import com.hhplus.concert_ticketing.business.service.impl.TokenQueueServiceImpl;
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
class SeatManagementFacadeImlTest {
    private static final Logger log = LoggerFactory.getLogger(SeatManagementFacadeImlTest.class);

    @InjectMocks
    SeatManagementFacadeIml seatManagementFacadeIml;

    @Mock
    SeatServiceImpl seatService;

    @Mock
    ConcertOptionServiceImpl concertOptionService;

    @Mock
    TokenQueueServiceImpl tokenQueueService;


    @DisplayName("토큰 유효성 체크, 토큰 상태가 ACTIVE일 경우")
    @Test
    void checking_token_and_stats_is_Active() {
        //given
        Long userId = 1L;
        String tokend = "adfasdf";
        LocalDateTime now = LocalDateTime.now();
        Token token = new Token(userId, "token123123", TokenStatus.ACTIVE.toString(), now, now.plusMinutes(10));
        when(tokenQueueService.validateTokenByToken(tokend)).thenReturn(token);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            seatManagementFacadeIml.getSeatData(userId, tokend);
        });
    }

    @DisplayName("유효성 체크 ConcertOption이 Null일 경우")
    @Test
    void validate_check_ConcertOption_is_null() {
        //given
        Long concertOptionId = 1L;
        String tokend = "adfasdf";
        LocalDateTime now = LocalDateTime.now();
        Token token = new Token(1L, "token123123", TokenStatus.ACTIVE.toString(), now, now.plusMinutes(10));
        when(tokenQueueService.validateTokenByToken(tokend)).thenReturn(token);
        when(concertOptionService.getConcertOptionDataByLocalDate(concertOptionId)).thenReturn(null);


        // when && then
        assertThrows(RuntimeException.class, () -> {
            seatManagementFacadeIml.getSeatData(concertOptionId, tokend);
        });
    }

    @DisplayName("유효성 체크 Seat가 Null일 경우")
    @Test
    void validate_check_Seat_is_null() {
        //given
        Long concertOptionId = 1L;
        String tokend = "adfasdf";
        LocalDateTime now = LocalDateTime.now();
        String status = SeatStatus.AVAILABLE.toString();
        Token token = new Token(1L, "token123123", TokenStatus.ACTIVE.toString(), now, now.plusMinutes(10));

        when(tokenQueueService.validateTokenByToken(tokend)).thenReturn(token);
        when(concertOptionService.getConcertOptionDataByLocalDate(concertOptionId))
                .thenReturn(new ConcertOption(1L, now, 10000.0));
        List<Seat> list = new ArrayList<>();
        when(seatService.getSeatData(concertOptionId, status)).thenReturn(list);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            seatManagementFacadeIml.getSeatData(concertOptionId, tokend);
        });
    }



    @DisplayName("예약좌석 정상 리턴")
    @Test
    void validate_check_Seat_is_Success() {
        //given
        Long concertOptionId = 2L;
        Long concertId = 2L;
        String tokend = "adfasdf";
        LocalDateTime now = LocalDateTime.now();
        String status = SeatStatus.AVAILABLE.toString();
        Token token = new Token(1L, "token123123", TokenStatus.ACTIVE.toString(), now, now.plusMinutes(10));

        when(tokenQueueService.validateTokenByToken(tokend)).thenReturn(token);
        ConcertOption concertOption = new ConcertOption(concertId, now.plusHours(1), 10000.0);
        when(concertOptionService.getConcertOptionDataByLocalDate(concertOptionId))
                .thenReturn(concertOption);

        List<Seat> seatData = List.of(
                new Seat(concertOptionId, "1A", SeatStatus.AVAILABLE.toString()),
                new Seat(concertOptionId, "2A", SeatStatus.AVAILABLE.toString()),
                new Seat(concertOptionId, "3A", SeatStatus.AVAILABLE.toString())
        );
        when(seatService.getSeatData(concertOptionId, status)).
                thenReturn(seatData);

        // when && then
        assertEquals(seatData.size(), seatManagementFacadeIml.getSeatData(concertOptionId, tokend).size());
    }

}
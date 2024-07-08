package com.hhplus.concert_ticketing.business.facade.impl;


import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.service.ConcertOptionService;
import com.hhplus.concert_ticketing.business.service.impl.SeatServiceImpl;
import com.hhplus.concert_ticketing.status.SeatStatus;
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
    ConcertOptionService concertOptionService;

    @DisplayName("유효성 체크 ConcertOption이 Null일 경우")
    @Test
    void validate_check_ConcertOption_is_null() {
        //given
        Long concertOptionId = 1L;
        Long tokenId = 1L;
        when(concertOptionService.getConcertOptionDataByLocalDate(concertOptionId)).thenReturn(null);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            seatManagementFacadeIml.getSeatData(concertOptionId, tokenId);
        });
    }

    @DisplayName("유효성 체크 Seat가 Null일 경우")
    @Test
    void validate_check_Seat_is_null() {
        //given
        Long concertOptionId = 1L;
        Long tokenId = 1L;
        LocalDateTime now = LocalDateTime.now();
        String status = SeatStatus.AVAILABLE.toString();
        when(concertOptionService.getConcertOptionDataByLocalDate(concertOptionId))
                .thenReturn(new ConcertOption(1L, now, 10000.0));
        List<Seat> list = new ArrayList<>();
        when(seatService.getSeatData(concertOptionId, status)).thenReturn(list);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            seatManagementFacadeIml.getSeatData(concertOptionId, tokenId);
        });
    }



    @DisplayName("예약좌석 정상 리턴")
    @Test
    void validate_check_Seat_is_Success() {
        //given
        Long concertOptionId = 2L;
        Long concertId = 2L;
        Long tokenId = 1L;
        LocalDateTime now = LocalDateTime.now();
        String status = SeatStatus.AVAILABLE.toString();

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
        assertEquals(seatData.size(), seatManagementFacadeIml.getSeatData(concertOptionId, tokenId).size());
    }

}
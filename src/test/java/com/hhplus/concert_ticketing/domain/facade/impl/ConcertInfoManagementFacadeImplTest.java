package com.hhplus.concert_ticketing.domain.facade.impl;

import com.hhplus.concert_ticketing.application.concert.impl.ConcertInfoManagementFacadeImpl;
import com.hhplus.concert_ticketing.domain.concert.entity.Concert;
import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import com.hhplus.concert_ticketing.infra.queue.impl.TokenRepositoryImpl;
import com.hhplus.concert_ticketing.domain.concert.service.impl.ConcertServiceImpl;
import com.hhplus.concert_ticketing.domain.queue.service.impl.TokenServiceImpl;
import com.hhplus.concert_ticketing.presentation.concert.dto.ConcertOptionDto;
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ConcertInfoManagementFacadeImplTest {

    private static final Logger log = LoggerFactory.getLogger(ConcertInfoManagementFacadeImplTest.class);

    @InjectMocks
    ConcertInfoManagementFacadeImpl concertInfoManagementFacade;

    @Mock
    TokenServiceImpl tokenService;

    @Mock
    ConcertServiceImpl concertService;

    @Mock
    TokenRepositoryImpl tokenRepository;


    @DisplayName("콘서트 유효성 체크, Concert가 null일 경우")
    @Test
    void checking_concert_is_null() throws Exception {
        //given
        Long concertId = 1L;
        when(concertService.getConcertData(concertId)).thenThrow(RuntimeException.class);

        //when && then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getConcertOption(concertId);
        });
    }


    @DisplayName("콘서트 옵션 체크, ConcertOption 리스트 크기가 0일 경우")
    @Test
    void checking_concertOption_list_size_0() throws Exception {
        //given
        Long concertId = 1L;
        Concert concert = new Concert("콘서트");
        when(concertService.getConcertData(concertId)).thenReturn(concert);

        //when
        when(concertService.getConcertOptionData(concertId)).thenThrow(RuntimeException.class);

        //then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getConcertOption(concertId);
        });
    }

    @DisplayName("콘서트 옵션 체크, ConcertOption 리스트 크기가 0보다 클 경우")
    @Test
    void checking_concertOption_list_size_1() throws Exception {
        //given
        Long concertId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Concert concert = new Concert("콘서트");
        when(concertService.getConcertData(concertId)).thenReturn(concert);

        //when
        List<ConcertOption> concertOptionList = List.of(
                new ConcertOption(1L, now, 10000.0),
                new ConcertOption(1L, now.minusHours(3), 14000.0),
                new ConcertOption(1L, now.plusHours(3), 12000.0)
        );

        when(concertService.getConcertOptionData(concertId)).thenReturn(concertOptionList);
        List<ConcertOptionDto> concertOption = concertInfoManagementFacade.getConcertOption(concertId);
        //then
        assertThat(concertOption).isEqualTo(concertOptionList);
    }

    @DisplayName("유효성 체크 ConcertOption이 Null일 경우")
    @Test
    void validate_check_ConcertOption_is_null() throws Exception {
        //given
        Long concertOptionId = 1L;
        LocalDateTime now = LocalDateTime.now();
        when(concertService.getConcertOptionDataByLocalDate(concertOptionId)).thenThrow(RuntimeException.class);


        // when && then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getSeatData(concertOptionId);
        });
    }

    @DisplayName("유효성 체크 Seat가 Null일 경우")
    @Test
    void validate_check_Seat_is_null() throws Exception {
        //given
        Long concertOptionId = 1L;
        LocalDateTime now = LocalDateTime.now();
        String status = SeatStatus.AVAILABLE.toString();

        when(concertService.getConcertOptionDataByLocalDate(concertOptionId))
                .thenReturn(new ConcertOption(1L, now, 10000.0));
        List<Seat> list = new ArrayList<>();
        when(concertService.getSeatData(concertOptionId, status)).thenThrow(RuntimeException.class);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getSeatData(concertOptionId);
        });
    }



    @DisplayName("예약좌석 정상 리턴")
    @Test
    void validate_check_Seat_is_Success() throws Exception {
        //given
        Long concertOptionId = 2L;
        Long concertId = 2L;
        LocalDateTime now = LocalDateTime.now();
        String status = SeatStatus.AVAILABLE.toString();

        ConcertOption concertOption = new ConcertOption(concertId, now.plusHours(1), 10000.0);
        when(concertService.getConcertOptionDataByLocalDate(concertOptionId))
                .thenReturn(concertOption);

        List<Seat> seatData = List.of(
                new Seat(concertOptionId, "1A", SeatStatus.AVAILABLE.toString()),
                new Seat(concertOptionId, "2A", SeatStatus.AVAILABLE.toString()),
                new Seat(concertOptionId, "3A", SeatStatus.AVAILABLE.toString())
        );
        when(concertService.getSeatData(concertOptionId, status)).
                thenReturn(seatData);

        // when && then
        assertEquals(seatData.size(), concertInfoManagementFacade.getSeatData(concertOptionId).size());
    }
}
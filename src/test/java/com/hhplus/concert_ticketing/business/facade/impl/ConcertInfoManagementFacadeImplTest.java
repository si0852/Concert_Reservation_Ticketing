package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.application.facade.impl.ConcertInfoManagementFacadeImpl;
import com.hhplus.concert_ticketing.business.entity.Concert;
import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.repository.impl.TokenRepositoryImpl;
import com.hhplus.concert_ticketing.business.service.impl.ConcertServiceImpl;
import com.hhplus.concert_ticketing.business.service.impl.TokenServiceImpl;
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

    @DisplayName("콘서트 유효성 체크, 토큰 유효성 체크")
    @Test
    void checking_concert_is_token_check() throws Exception{
        //given
        String token = "asdfasdfsa";
        Long concertId = 1L;
        LocalDateTime now = LocalDateTime.now();
        when(tokenService.validateTokenByToken(token)).thenThrow(RuntimeException.class);

        //when && then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getConcertOption(token, concertId);
        });
    }


    @DisplayName("콘서트 유효성 체크, Concert가 null일 경우")
    @Test
    void checking_concert_is_null() throws Exception {
        //given
        String token = "asdfasdfsa";
        Long concertId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token t = new Token(1L, token, TokenStatus.ACTIVE.toString(), now, now.plusHours(1));
        when(tokenService.validateTokenByToken(token)).thenReturn(t);
        when(concertService.getConcertData(concertId)).thenThrow(RuntimeException.class);

        //when && then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getConcertOption(token, concertId);
        });
    }


    @DisplayName("콘서트 옵션 체크, ConcertOption 리스트 크기가 0일 경우")
    @Test
    void checking_concertOption_list_size_0() throws Exception {
        //given
        String token = "asdfasdfsa";
        Long concertId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token t = new Token(1L, token, TokenStatus.ACTIVE.toString(), now, now.plusHours(1));
        when(tokenService.validateTokenByToken(token)).thenReturn(t);
        Concert concert = new Concert("콘서트");
        when(concertService.getConcertData(concertId)).thenReturn(concert);

        //when
//        List<ConcertOption> concertOptionList = List.of();
        when(concertService.getConcertOptionData(concertId)).thenThrow(RuntimeException.class);

        //then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getConcertOption(token, concertId);
        });
    }

    @DisplayName("콘서트 옵션 체크, ConcertOption 리스트 크기가 0보다 클 경우")
    @Test
    void checking_concertOption_list_size_1() throws Exception {
        //given
        String token = "asdfasdfsa";
        Long concertId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token t = new Token(1L, token, TokenStatus.ACTIVE.toString(), now, now.plusHours(1));
        when(tokenService.validateTokenByToken(token)).thenReturn(t);
        Concert concert = new Concert("콘서트");
        when(concertService.getConcertData(concertId)).thenReturn(concert);

        //when
        List<ConcertOption> concertOptionList = List.of(
                new ConcertOption(1L, now, 10000.0),
                new ConcertOption(1L, now.minusHours(3), 14000.0),
                new ConcertOption(1L, now.plusHours(3), 12000.0)
        );

        when(concertService.getConcertOptionData(concertId)).thenReturn(concertOptionList);
        List<ConcertOption> concertOption = concertInfoManagementFacade.getConcertOption(token, concertId);
        //then
        assertThat(concertOption).isEqualTo(concertOptionList);
    }

    @DisplayName("토큰 유효성 체크, 토큰 상태가 ACTIVE일 경우")
    @Test
    void checking_token_and_stats_is_Active() throws Exception {
        //given
        Long userId = 1L;
        String tokend = "adfasdf";
        LocalDateTime now = LocalDateTime.now();
        when(tokenService.validateTokenByToken(tokend)).thenThrow(RuntimeException.class);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getSeatData(userId, tokend);
        });
    }

    @DisplayName("유효성 체크 ConcertOption이 Null일 경우")
    @Test
    void validate_check_ConcertOption_is_null() throws Exception {
        //given
        Long concertOptionId = 1L;
        String tokend = "adfasdf";
        LocalDateTime now = LocalDateTime.now();
        Token token = new Token(1L, "token123123", TokenStatus.ACTIVE.toString(), now, now.plusMinutes(10));
        when(concertService.getConcertOptionDataByLocalDate(concertOptionId)).thenThrow(RuntimeException.class);


        // when && then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getSeatData(concertOptionId, tokend);
        });
    }

    @DisplayName("유효성 체크 Seat가 Null일 경우")
    @Test
    void validate_check_Seat_is_null() throws Exception {
        //given
        Long concertOptionId = 1L;
        String tokend = "adfasdf";
        LocalDateTime now = LocalDateTime.now();
        String status = SeatStatus.AVAILABLE.toString();
        Token token = new Token(1L, "token123123", TokenStatus.ACTIVE.toString(), now, now.plusMinutes(10));

        when(tokenService.validateTokenByToken(tokend)).thenReturn(token);
        when(concertService.getConcertOptionDataByLocalDate(concertOptionId))
                .thenReturn(new ConcertOption(1L, now, 10000.0));
        List<Seat> list = new ArrayList<>();
        when(concertService.getSeatData(concertOptionId, status)).thenThrow(RuntimeException.class);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getSeatData(concertOptionId, tokend);
        });
    }



    @DisplayName("예약좌석 정상 리턴")
    @Test
    void validate_check_Seat_is_Success() throws Exception {
        //given
        Long concertOptionId = 2L;
        Long concertId = 2L;
        String tokend = "adfasdf";
        LocalDateTime now = LocalDateTime.now();
        String status = SeatStatus.AVAILABLE.toString();
        Token token = new Token(1L, "token123123", TokenStatus.ACTIVE.toString(), now, now.plusMinutes(10));

        when(tokenService.validateTokenByToken(tokend)).thenReturn(token);
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
        assertEquals(seatData.size(), concertInfoManagementFacade.getSeatData(concertOptionId, tokend).size());
    }
}
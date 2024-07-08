package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Concert;
import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.service.impl.ConcertOptionServiceImpl;
import com.hhplus.concert_ticketing.business.service.impl.ConcertServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
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
    ConcertOptionServiceImpl concertOptionService;

    @Mock
    ConcertServiceImpl concertService;


    @DisplayName("콘서트 유효성 체크, Concert가 null일 경우")
    @Test
    void checking_concert_is_null() {
        //given
        Long tokenId = 1L;
        Long concertId = 1L;
        when(concertService.getConcertData(concertId)).thenReturn(null);

        //when && then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getConcertOption(tokenId, concertId);
        });
    }

    @DisplayName("콘서트 유효성 체크, Concert가 null일 경우 (토큰 상태가 만료일 경우)")
    @Test
    void checking_concert_is_null2() {
        //given
        Long tokenId = 1L;
        Long concertId = 1L;
        when(concertService.getConcertData(concertId)).thenReturn(null);

        //when && then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getConcertOption(tokenId, concertId);
        });
    }

    @DisplayName("콘서트 옵션 체크, ConcertOption 리스트 크기가 0일 경우")
    @Test
    void checking_concertOption_list_size_0() {
        //given
        Long tokenId = 1L;
        Long concertId = 1L;
        Concert concert = new Concert("콘서트");
        when(concertService.getConcertData(concertId)).thenReturn(concert);

        //when
        List<ConcertOption> concertOptionList = List.of();
        when(concertOptionService.getConcertOptionData(concertId)).thenReturn(concertOptionList);

        //then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getConcertOption(tokenId, concertId);
        });
    }

    @DisplayName("콘서트 옵션 체크, ConcertOption 리스트 크기가 0보다 클 경우")
    @Test
    void checking_concertOption_list_size_1() {
        //given
        Long tokenId = 1L;
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

        when(concertOptionService.getConcertOptionData(concertId)).thenReturn(concertOptionList);
        List<ConcertOption> concertOption = concertInfoManagementFacade.getConcertOption(tokenId, concertId);
        //then
        assertThat(concertOption).isEqualTo(concertOptionList);
    }
}
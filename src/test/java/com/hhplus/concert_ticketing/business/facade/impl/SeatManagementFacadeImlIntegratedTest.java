package com.hhplus.concert_ticketing.business.facade.impl;


import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.facade.SeatManagementFacade;
import com.hhplus.concert_ticketing.business.service.ConcertOptionService;
import com.hhplus.concert_ticketing.business.service.SeatService;
import com.hhplus.concert_ticketing.business.service.TokenQueueService;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.business.service.impl.ConcertOptionServiceImpl;
import com.hhplus.concert_ticketing.business.service.impl.SeatServiceImpl;
import com.hhplus.concert_ticketing.business.service.impl.TokenQueueServiceImpl;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class SeatManagementFacadeImlIntegratedTest {
    private static final Logger log = LoggerFactory.getLogger(SeatManagementFacadeImlIntegratedTest.class);

    @Autowired
    SeatManagementFacade seatManagementFacade;

    @Autowired
    TokenQueueService tokenQueueService;

    @Autowired
    SeatService seatService;

    @Autowired
    ConcertOptionService concertOptionService;

    @Autowired
    TokenService tokenService;


    @DisplayName("정상 로직 테스트")
    @Test
    void logic_test() {
        //given
        LocalDateTime now = LocalDateTime.now();
        Token token1 = tokenService.generateToken(1L);
        token1.setStatus(TokenStatus.ACTIVE.toString());
        Token token = tokenQueueService.saveToken(token1);
        ConcertOption concertOption = concertOptionService.saveConcertOption(new ConcertOption(11L, now.plusDays(1), 10000.0));
        log.info("concertOption : " + concertOption);
        ConcertOption selectData = concertOptionService.getConcertOptionDataById(concertOption.getConcertOptionId());
        log.info("selectData : " + selectData);
        Seat seat = seatService.saveSeatData(new Seat(concertOption.getConcertOptionId(), "1A", SeatStatus.AVAILABLE.toString()));

        // when
        List<Seat> seatData = seatManagementFacade.getSeatData(concertOption.getConcertOptionId(), token.getToken());

        //then
        assertEquals(seatData.get(0).getSeatId(), seat.getSeatId());
    }

}
package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Concert;
import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.application.facade.ConcertInfoManagementFacade;
import com.hhplus.concert_ticketing.business.service.ConcertOptionService;
import com.hhplus.concert_ticketing.business.service.ConcertService;
import com.hhplus.concert_ticketing.business.service.TokenQueueService;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.infra.JpaConcertOptionRepository;
import com.hhplus.concert_ticketing.infra.JpaConcertRepository;
import com.hhplus.concert_ticketing.infra.JpaTokenRepository;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class ConcertInfoManagementFacadeImplIntegratedTest {

    private static final Logger log = LoggerFactory.getLogger(ConcertInfoManagementFacadeImplIntegratedTest.class);

    @Autowired
    ConcertInfoManagementFacade concertInfoManagementFacade;

    @Autowired
    ConcertOptionService concertOptionService;

    @Autowired
    TokenQueueService tokenQueueService;

    @Autowired
    TokenService tokenService;

    @Autowired
    ConcertService concertService;

    @Autowired
    JpaTokenRepository tokenRepository;

    @Autowired
    JpaConcertRepository jpaConcertRepository;

    @Autowired
    JpaConcertOptionRepository jpaConcertOptionRepository;

    @BeforeEach
    void set_up() {
        tokenRepository.deleteAll();
        jpaConcertRepository.deleteAll();
        jpaConcertOptionRepository.deleteAll();
    }


    @DisplayName("logicTest: 토큰상태가 Waiting 일때")
    @Test
    void test_token_status_is_active() {
        //given
        Token tokenInfo = tokenService.generateToken(1L);
        Token saveToken = tokenQueueService.saveToken(tokenInfo);

        Token token = tokenQueueService.validateTokenByTokenId(saveToken.getTokenId());
        token.setStatus(TokenStatus.WAITING.toString());
        tokenQueueService.updateToken(token);
        Long concertId = 1L;

        //when && then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getConcertOption(token.getToken(), concertId);
        });
    }

    @DisplayName("logicTest: 콘서트 정보가 없을때")
    @Test
    void test_no_concert_info() {
        //given
        Token token1 = tokenService.generateToken(2L);
        Token saveToken = tokenQueueService.saveToken(token1);

        Token token = tokenQueueService.validateTokenByTokenId(saveToken.getTokenId());
        token.setStatus(TokenStatus.ACTIVE.toString());
        tokenQueueService.updateToken(token);
        Long concertId = 1L;

        //when && then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getConcertOption(token.getToken(), concertId);
        });
    }

    @DisplayName("logicTest: 콘서트 상세 정보가 없을때")
    @Test
    void test_no_concert_option_info() {
        //given
        Token token1 = tokenService.generateToken(3L);
        Token saveToken = tokenQueueService.saveToken(token1);
        Token token = tokenQueueService.validateTokenByTokenId(saveToken.getTokenId());
        token.setStatus(TokenStatus.ACTIVE.toString());
        tokenQueueService.updateToken(token);

        Concert concert = concertService.saveConcertData(new Concert("Party"));

        //when
        Long concertId = concert.getConcertId();
        // then
        assertThrows(RuntimeException.class, () -> {
            concertInfoManagementFacade.getConcertOption(token.getToken(), concertId);
        });
    }

    @DisplayName("logicTest: 동작여부")
    @Test
    void logic_test() {
        //given
        Token token1 = tokenService.generateToken(4L);
        Token saveToken = tokenQueueService.saveToken(token1);
        Token token = tokenQueueService.validateTokenByTokenId(saveToken.getTokenId());
        token.setStatus(TokenStatus.ACTIVE.toString());
        tokenQueueService.updateToken(token);

        Concert concert = concertService.saveConcertData(new Concert("Party"));

        Long concertId = concert.getConcertId();
        LocalDateTime now = LocalDateTime.now();

        concertOptionService.saveConcertOption(new ConcertOption(concertId, now.plusHours(1), 10000.0));
        concertOptionService.saveConcertOption(new ConcertOption(concertId, now.plusHours(3), 10000.0));
        concertOptionService.saveConcertOption(new ConcertOption(concertId, now.plusHours(5), 10000.0));

        //when
        List<ConcertOption> concertOption = concertInfoManagementFacade.getConcertOption(token.getToken(), concertId);
        // then
        assertThat(concertOption.size()).isEqualTo(3);
    }



}
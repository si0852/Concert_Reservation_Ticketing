package com.hhplus.concert_ticketing.domain.facade.impl;

import com.hhplus.concert_ticketing.application.facade.TokenManagementFacade;
import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.domain.queue.service.TokenService;

import com.hhplus.concert_ticketing.infra.queue.JpaTokenRepository;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TokenManagementFacadeImplIntegratedTest {

    private static final Logger log = LoggerFactory.getLogger(TokenManagementFacadeImplIntegratedTest.class);

    @Autowired
    TokenManagementFacade tokenManagementFacade;

    @Autowired
    TokenService tokenService;


    @Autowired
    JpaTokenRepository jpaTokenRepository;


    private void set_up() {
        Token token = tokenService.generateToken(1L);
        tokenService.saveToken(token);

        Token token2 = tokenService.generateToken(2L);
        tokenService.saveToken(token2);

        Token token3 = tokenService.generateToken(3L);
        token3.changeActive();
        tokenService.saveToken(token3);

        Token token4 = tokenService.generateToken(4L);
        token4.changeActive();
        tokenService.saveToken(token4);

        Token token5 = tokenService.generateToken(5L);
        tokenService.saveToken(token5);

        Token token6 = tokenService.generateToken(6L);
        token6.changeActive();
        tokenService.saveToken(token6);

        Token token7 = tokenService.generateToken(7L);
        tokenService.saveToken(token7);

        Token token8 = tokenService.generateToken(8L);
        tokenService.saveToken(token8);

        Token token9 = tokenService.generateToken(9L);
        token9.changeExpired();
        tokenService.saveToken(token9);

    }


    @DisplayName("토큰 발급 통합 Test: 주어진 userId 관련 토큰이 null 인 경우+대기열에 자리가 남을경우 -> Active 상태로 저장 예상")
    @Test
    void token_insert_test() {

        //given
        set_up();
        Long userId = 10L;

        //when
        Token token = tokenManagementFacade.insertToken(userId);

        //then
        assertThat(token.getStatus()).isEqualTo(TokenStatus.ACTIVE.toString());
        jpaTokenRepository.deleteAll();

    }

    @DisplayName("토큰 발급 통합 Test: 주어진 userId 관련 토큰이 null 인 경우+대기열에 자리가 없을경우 -> Waiting 상태로 저장 예상")
    @Test
    void token_insert_test2() {

        //given
        set_up();
        Long userId = 12L;
        Token token9 = tokenService.generateToken(10L);
        token9.changeActive();
        tokenService.saveToken(token9);
        Token token10 = tokenService.generateToken(11L);
        tokenService.saveToken(token10);


        //when
        Token token = tokenManagementFacade.insertToken(userId);

        //then
        assertThat(token.getStatus()).isEqualTo(TokenStatus.WAITING.toString());
        jpaTokenRepository.deleteAll();

    }

    @DisplayName("토큰 발급 통합 Test: 주어진 userId 관련 토큰 상태가 expired 일 경우, 발급된 토큰 값이 같을 것이다.")
    @Test
    void token_insert_test_expired() {

        //given
        set_up();
        Long userId = 9L;

        //when
        Token getToken = tokenService.validateToken(userId);
        Token token = tokenManagementFacade.insertToken(userId);

        //then
        assertThat(getToken.getStatus()).isEqualTo(TokenStatus.EXPIRED.toString());
        assertThat(token.getToken()).isEqualTo(getToken.getToken());
        jpaTokenRepository.deleteAll();

    }


    @DisplayName("토큰 위치 리턴")
    @Test
    void token_get_position() {
        //given
        set_up();
        Token token = tokenService.validateToken(8L);
        String tokend = token.getToken();

        //when
        Integer tokenPosition = tokenManagementFacade.getTokenPosition(tokend);

        //then
        assertThat(tokenPosition).isEqualTo(5);
        jpaTokenRepository.deleteAll();
    }

    @DisplayName("토큰 정보 리턴")
    @Test
    void token_get_info() throws Exception {
        //given
        set_up();
        Token token = tokenService.validateToken(8L);
        String tokend = token.getToken();

        //when
        Token getToken = tokenManagementFacade.getTokenInfo(tokend);

        //then
        assertThat(getToken.getTokenId()).isEqualTo(token.getTokenId());
        jpaTokenRepository.deleteAll();
    }


    @AfterEach
    void after() {

        jpaTokenRepository.deleteAll();
    }
}
package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.repository.TokenRepository;
import com.hhplus.concert_ticketing.business.service.TokenQueueService;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.business.service.impl.TokenQueueServiceImpl;
import com.hhplus.concert_ticketing.business.service.impl.TokenServiceImpl;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TokenManagementFacadeImplTest {

    private static final Logger log = LoggerFactory.getLogger(TokenManagementFacadeImplTest.class);

    @InjectMocks
    TokenManagementFacadeImpl tokenManagementFacade;

    @Mock
    TokenServiceImpl tokenService;

    @Mock
    TokenQueueServiceImpl tokenQueueService;



    @DisplayName("토큰 저장, 토큰 조회시 Null 일경우")
    @Test
    void select_token_is_null() {
        //given
        Long userId = 1L;
        when(tokenQueueService.validateToken(userId)).thenReturn(null);

        // when
        Token generatedToken = tokenService.generateToken(userId);
        Token savedToken = tokenManagementFacade.insertToken(userId);
        // then
        assertThat(generatedToken).isSameAs(savedToken);
    }

    @DisplayName("토큰 저장, 토큰 조회시 Null이 아니지만 상태가 Active일때")
    @Test
    void select_token_is_not_null_and_Active() {
        //given
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token token = new Token(userId, "token123123", TokenStatus.ACTIVE.toString(), now, now.plusMinutes(10));
        when(tokenQueueService.validateToken(userId)).thenReturn(token);

        //when && then
        assertThrows(RuntimeException.class, () -> {
            tokenManagementFacade.insertToken(userId);
        });
    }

    @DisplayName("토큰 저장, 토큰 조회시 Null이 아니지만 상태가 Expired일 경우")
    @Test
    void select_token_is_not_null_and_Expired() {
        //given
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token token = new Token(userId, "token123123", TokenStatus.EXPIRED.toString(), now, now.plusMinutes(10));
        when(tokenQueueService.validateToken(userId)).thenReturn(token);

        //when
        LocalDateTime after = now.plusMinutes(30);
        Token updateToken = new Token(userId, "token123123", TokenStatus.WAITING.toString(), after, after.plusMinutes(10));
        when(tokenService.generateToken(userId, token.getToken())).thenReturn(updateToken);
        when(tokenQueueService.saveToken(updateToken)).thenReturn(updateToken);

        //then
        assertThat(tokenManagementFacade.insertToken(userId)).isEqualTo(updateToken);
    }

    @DisplayName("토큰 저장, 토큰 조회시 Null이 아니지만 상태가 Waiting일 경우")
    @Test
    void select_token_is_not_null_and_Waiting() {
        //given
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token token = new Token(userId, "token123123", TokenStatus.EXPIRED.toString(), now, now.plusMinutes(10));
        when(tokenQueueService.validateToken(userId)).thenReturn(token);

        // when && then
        assertThat(tokenManagementFacade.insertToken(userId)).isEqualTo(token);
    }

}
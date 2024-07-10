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
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

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

    private final Integer maxActiveTokens = 30;
    // userId 통해 조회된 token 정보 null 일때, generateToken = generateToken
    // userId 통해 조회된 token 정보 null 아닐때, runtimeException or generateToken


    @DisplayName("토큰 저장, 토큰 조회시 값이 Null 이고 대기열에 자리가 있다면 상태가 active인 토큰이 저장된다")
    @Test
    void select_token_is_null_active() {
        //given
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token generateToken = new Token(userId, "token123123", TokenStatus.WAITING.toString(), now, now.plusMinutes(10));

        //when
        int activeSpace = 5;
        int spaceQueue = activeSpace - 2;

        if (activeSpace > 0 && spaceQueue > 0) {
            generateToken.setStatus(TokenStatus.ACTIVE.toString());
        }

        //then
        assertThat(generateToken.getStatus()).isEqualTo(TokenStatus.ACTIVE.toString());
    }

    @DisplayName("토큰 저장, 토큰 조회시 값이 Null 이고 대기열에 자리가 없다면 상태가 waiting 상태인 토큰이 저장된다")
    @Test
    void select_token_is_null_waiting() {
        //given
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Token generateToken = new Token(userId, "token123123", TokenStatus.WAITING.toString(), now, now.plusMinutes(10));

        //when
        int activeSpace = 5;
        int spaceQueue = activeSpace - 5;

        if (activeSpace > 0 && spaceQueue > 0) {
            generateToken.setStatus(TokenStatus.ACTIVE.toString());
        }

        //then
        assertThat(generateToken.getStatus()).isEqualTo(TokenStatus.WAITING.toString());
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

    @DisplayName("대기열 위치 찾기")
    @Test
    void find_index() {
        LocalDateTime now = LocalDateTime.now();
        List<Token> list = List.of(
                new Token(1L, "token123123", TokenStatus.WAITING.toString(), now, now.plusMinutes(10)),
                new Token(2L, "sadfasdfas2", TokenStatus.WAITING.toString(), now, now.plusMinutes(10)),
                new Token(3L, "sdfasdf2123", TokenStatus.WAITING.toString(), now, now.plusMinutes(10)),
                new Token(4L, "asdfsdfs3xz", TokenStatus.WAITING.toString(), now, now.plusMinutes(10)),
                new Token(5L, "vcvse323ass", TokenStatus.WAITING.toString(), now, now.plusMinutes(10)),
                new Token(6L, "asdgseg32sa", TokenStatus.WAITING.toString(), now, now.plusMinutes(10))
        );

        int index = IntStream.range(0, list.size())
                .filter(data -> Objects.equals(list.get(data).getUserId(), 2L))
                .findFirst().orElse(-1);

        assertEquals(1, index);
    }

    @DisplayName("폴링: 상태값 찾기 - 토큰 정보 null일떄")
    @Test
    void polling_find_status_null() {
        //given
        String token = "token123123";
        when(tokenQueueService.validateTokenByToken(token)).thenReturn(null);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            tokenManagementFacade.getTokenInfo(token);
        });
    }

    @DisplayName("폴링: 상태값 찾기 - 토큰 상태가 Expired 일때")
    @Test
    void polling_find_status_expired() {
        //given
        String token = "token123123";
        Token tokenInfo = new Token(1L, token, TokenStatus.EXPIRED.toString(), null, null);
        when(tokenQueueService.validateTokenByToken(token)).thenReturn(tokenInfo);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            tokenManagementFacade.getTokenInfo(token);
        });
    }

    @DisplayName("폴링: 상태값 찾기 - 토큰 상태가 Active 일때")
    @Test
    void polling_find_status_active() {
        //given
        String token = "token123123";
        Token tokenInfo = new Token(1L, token, TokenStatus.ACTIVE.toString(), null, null);
        when(tokenQueueService.validateTokenByToken(token)).thenReturn(tokenInfo);

        // when && then
        assertThrows(RuntimeException.class, () -> {
            tokenManagementFacade.getTokenInfo(token);
        });
    }


}
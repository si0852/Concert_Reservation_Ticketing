package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.facade.TokenManagementFacade;
import com.hhplus.concert_ticketing.business.service.TokenQueueService;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TokenManagementFacadeImpl implements TokenManagementFacade {

    private final TokenService tokenService;
    private final TokenQueueService tokenQueueService;

    public TokenManagementFacadeImpl(TokenService tokenService, TokenQueueService tokenQueueService) {
        this.tokenService = tokenService;
        this.tokenQueueService = tokenQueueService;
    }


    @Transactional
    @Override
    public Token insertToken(Long userId) {
        // 토큰 존재여부 확인
        Token token = tokenQueueService.validateToken(userId);
        Token generatedToken = null;
        // 토큰 발급
        if(token == null) generatedToken = tokenService.generateToken(userId);
        // 토큰이 이미 존재 && 활성화 상태이면 예약중이므로 익셉션 발생
        if(token != null && (token.getStatus().equals(TokenStatus.ACTIVE.toString()))) throw new RuntimeException("이미 예약중인 데이터가 존재합니다.");

        // 토큰이 이미 존재 && 만료 상태이면 발급날짜만 발급
        else if (token != null && token.getStatus().equals(TokenStatus.EXPIRED.toString())) generatedToken = tokenService.generateToken(userId, token.getToken());


        return generatedToken == null ? token : tokenQueueService.saveToken(generatedToken);
    }
}

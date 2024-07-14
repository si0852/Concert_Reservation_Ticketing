package com.hhplus.concert_ticketing.business.service.impl;

import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {


    @Override
    public Token generateToken(Long userId) {
        String token = UUID.randomUUID().toString();
        String status = TokenStatus.WAITING.toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = now;
        LocalDateTime expiresAt = now.plusHours(1);
        return new Token(userId, token, status, createdAt, expiresAt);
    }

    @Override
    public Token generateToken(Long userId, String token) {
        String status = TokenStatus.WAITING.toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = now;
        LocalDateTime expiresAt = now.plusHours(1);
        return new Token(userId, token, status, createdAt, expiresAt);
    }
}

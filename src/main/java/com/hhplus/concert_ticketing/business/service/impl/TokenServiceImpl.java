package com.hhplus.concert_ticketing.business.service.impl;

import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.repository.TokenRepository;
import com.hhplus.concert_ticketing.business.service.TokenService;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public Token saveToken(Token token) {
        return tokenRepository.saveToken(token);
    }

    @Override
    public Token validateToken(Long userId) {
        return tokenRepository.getToken(userId);
    }

    @Override
    public Token updateToken(Token token) {
        return tokenRepository.updateToken(token);
    }
}

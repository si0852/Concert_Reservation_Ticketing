package com.hhplus.concert_ticketing.business.service.impl;

import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.repository.TokenRepository;
import com.hhplus.concert_ticketing.business.service.TokenQueueService;
import org.springframework.stereotype.Service;

@Service
public class TokenQueueServiceImpl implements TokenQueueService {

    private final TokenRepository tokenRepository;

    public TokenQueueServiceImpl(TokenRepository tokenRepository) {
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
    public Token validateTokenByTokenId(Long tokenId) {
        return tokenRepository.getTokenByTokenId(tokenId);
    }

    @Override
    public Token updateToken(Token token) {
        return tokenRepository.updateToken(token);
    }
}

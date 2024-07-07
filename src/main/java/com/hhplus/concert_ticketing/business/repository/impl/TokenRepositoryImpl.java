package com.hhplus.concert_ticketing.business.repository.impl;

import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.repository.TokenRepository;
import com.hhplus.concert_ticketing.infra.JpaTokenRepository;
import org.springframework.stereotype.Component;

@Component
public class TokenRepositoryImpl implements TokenRepository {

    private final JpaTokenRepository jpaTokenRepository;

    public TokenRepositoryImpl(JpaTokenRepository jpaTokenRepository) {
        this.jpaTokenRepository = jpaTokenRepository;
    }

    @Override
    public Token saveToken(Token token) {
        return jpaTokenRepository.save(token);
    }

    @Override
    public Token getToken(Long userId) {
        return jpaTokenRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public Token updateToken(Token token) {
        return jpaTokenRepository.save(token);
    }
}

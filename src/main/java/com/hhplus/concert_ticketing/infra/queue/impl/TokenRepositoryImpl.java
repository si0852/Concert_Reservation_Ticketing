package com.hhplus.concert_ticketing.infra.queue.impl;

import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.domain.queue.repository.TokenRepository;
import com.hhplus.concert_ticketing.infra.queue.JpaTokenRepository;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public Token getTokenByTokenId(Long tokenId) {
        return jpaTokenRepository.findByTokenId(tokenId).orElse(null);
    }

    @Override
    public Token getTokenByToken(String token) {
        return jpaTokenRepository.findByToken(token).orElse(null);
    }

    @Override
    public Token updateToken(Token token) {
        return jpaTokenRepository.save(token);
    }

    @Override
    public List<Token> getTokenListByStatus(String status) {
        return jpaTokenRepository.findAllByStatusOrderByCreatedAt(status);
    }
}

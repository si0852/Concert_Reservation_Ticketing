package com.hhplus.concert_ticketing.domain.queue.repository;

import com.hhplus.concert_ticketing.domain.queue.entity.Token;

import java.util.List;

public interface TokenRepository {

    Token saveToken(Token token);

    Token getToken(Long userId);

    Token getTokenByTokenId(Long tokenId);

    Token getTokenByToken(String token);

    Token updateToken(Token Token);

    List<Token> getTokenListByStatus(String status);
}

package com.hhplus.concert_ticketing.domain.queue.service;

import com.hhplus.concert_ticketing.domain.queue.entity.Token;

import java.util.List;

public interface TokenService {

    Token validateToken(Long userId);
    Token validateToken(String token);
    Token generateToken(Long userId);
    Token generateToken(Long userId, String token);

    Integer getTokenPosition(String token);

    Token saveToken(Token token);


    Token validateTokenByTokenId(Long tokenId);

    Token validateTokenByToken(String token) throws Exception;

    Token updateToken(Token token);

    List<Token> getTokenListByStatus(String status);
}

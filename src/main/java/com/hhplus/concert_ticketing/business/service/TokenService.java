package com.hhplus.concert_ticketing.business.service;

import com.hhplus.concert_ticketing.business.entity.Token;

import java.util.List;

public interface TokenService {

    Token generateToken(Long userId);
    Token generateToken(Long userId, String token);
    Token saveToken(Token token);

    Token validateToken(Long userId);

    Token validateTokenByTokenId(Long tokenId);

    Token validateTokenByToken(String token) throws Exception;

    Token updateToken(Token token);

    List<Token> getTokenListByStatus(String status);
}

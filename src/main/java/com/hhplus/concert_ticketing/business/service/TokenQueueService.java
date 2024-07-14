package com.hhplus.concert_ticketing.business.service;

import com.hhplus.concert_ticketing.business.entity.Token;

import java.util.List;

public interface TokenQueueService {

    Token saveToken(Token token);

    Token validateToken(Long userId);

    Token validateTokenByTokenId(Long tokenId);

    Token validateTokenByToken(String token);

    Token updateToken(Token token);

    List<Token> getTokenListByStatus(String status);
}

package com.hhplus.concert_ticketing.business.service;

import com.hhplus.concert_ticketing.business.entity.Token;

public interface TokenQueueService {

    Token saveToken(Token token);

    Token validateToken(Long userId);

    Token updateToken(Token token);
}

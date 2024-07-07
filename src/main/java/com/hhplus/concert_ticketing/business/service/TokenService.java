package com.hhplus.concert_ticketing.business.service;

import com.hhplus.concert_ticketing.business.entity.Token;

public interface TokenService {

    public Token saveToken(Token token);

    public Token validateToken(Long userId);

    public Token updateToken(Token token);
}

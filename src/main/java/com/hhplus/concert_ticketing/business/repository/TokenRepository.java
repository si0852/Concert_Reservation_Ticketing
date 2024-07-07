package com.hhplus.concert_ticketing.business.repository;

import com.hhplus.concert_ticketing.business.entity.Token;

public interface TokenRepository {

    Token saveToken(Token token);

    Token getToken(Long userId);

    Token updateToken(Token Token);
}

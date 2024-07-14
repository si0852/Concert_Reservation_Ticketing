package com.hhplus.concert_ticketing.business.service;

import com.hhplus.concert_ticketing.business.entity.Token;

public interface TokenService {

    Token generateToken(Long userId);
    Token generateToken(Long userId, String token);
}

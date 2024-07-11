package com.hhplus.concert_ticketing.business.facade;

import com.hhplus.concert_ticketing.business.entity.Token;

public interface TokenManagementFacade {

    Token insertToken(Long userId);

    Integer getTokenPosition(String token);

    Token getTokenInfo(String token);
}

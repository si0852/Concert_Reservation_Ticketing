package com.hhplus.concert_ticketing.business.facade;

import com.hhplus.concert_ticketing.business.entity.Token;

public interface TokenManagementFacade {

    Token insertToken(Long userId);

    Integer getTokenPosition(Long userId);

    Token getTokenInfo(String token);
}

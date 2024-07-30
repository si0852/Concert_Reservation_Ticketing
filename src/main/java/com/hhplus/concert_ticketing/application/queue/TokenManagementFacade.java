package com.hhplus.concert_ticketing.application.queue;

import com.hhplus.concert_ticketing.domain.queue.entity.Token;

public interface TokenManagementFacade {

    Token insertToken(Long userId);

    Integer getTokenPosition(String token);

    Token getTokenInfo(String token) throws Exception;
}

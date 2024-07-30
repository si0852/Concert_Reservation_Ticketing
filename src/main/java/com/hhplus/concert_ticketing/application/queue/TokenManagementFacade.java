package com.hhplus.concert_ticketing.application.queue;

import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.presentation.queue.dto.TokenDto;

public interface TokenManagementFacade {

    Token insertToken(Long userId);

    TokenDto getTokenData(String token);

    Integer getTokenPosition(String token);

    Token getTokenInfo(String token) throws Exception;
}

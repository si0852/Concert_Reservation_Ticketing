package com.hhplus.concert_ticketing.application.queue;

import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.presentation.queue.dto.TokenDto;
import com.hhplus.concert_ticketing.presentation.queue.dto.UserDto;

public interface TokenManagementFacade {

    UserDto addToQueue(String userId);

    UserDto getQueueDataByUser(String userId);

    Token insertToken(Long userId);

    TokenDto getTokenData(String token);

    Integer getTokenPosition(String token);

    Token getTokenInfo(String token) throws Exception;
}

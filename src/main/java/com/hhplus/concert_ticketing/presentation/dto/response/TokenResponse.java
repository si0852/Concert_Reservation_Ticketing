package com.hhplus.concert_ticketing.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TokenResponse {

    String          token;
    Integer         queuePosition;
    LocalDateTime   expired_at;

    @Builder
    public TokenResponse(String token, Integer queuePosition, LocalDateTime expired_at) {
        this.token = token;
        this.queuePosition = queuePosition;
        this.expired_at = expired_at;
    }

}

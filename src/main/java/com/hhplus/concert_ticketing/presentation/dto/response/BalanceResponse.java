package com.hhplus.concert_ticketing.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BalanceResponse {

    Long totalBalance;

    @Builder
    public BalanceResponse(Long totalBalance) {
        this.totalBalance = totalBalance;
    }
}

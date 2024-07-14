package com.hhplus.concert_ticketing.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BalanceResponse {

    Double totalBalance;

    @Builder
    public BalanceResponse(Double totalBalance) {
        this.totalBalance = totalBalance;
    }
}

package com.hhplus.concert_ticketing.presentation.dto.request;


import lombok.Data;

@Data
public class BalanceRequest {
    Long userId;
    Double amount;
}

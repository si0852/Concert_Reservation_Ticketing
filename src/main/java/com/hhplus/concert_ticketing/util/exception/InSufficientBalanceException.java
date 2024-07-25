package com.hhplus.concert_ticketing.util.exception;

import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;

public class InSufficientBalanceException extends RuntimeException{

    private final ResponseDto responseDto;


    public InSufficientBalanceException(ResponseDto responseDto) {
        this.responseDto = responseDto;
    }

    public ResponseDto getResponseDto() {
        return responseDto;
    }
}

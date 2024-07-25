package com.hhplus.concert_ticketing.util.exception;

import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;

public class LockException extends RuntimeException{

    private final ResponseDto responseDto;


    public LockException(ResponseDto responseDto) {
        this.responseDto = responseDto;
    }

    public ResponseDto getResponseDto() {
        return responseDto;
    }
}

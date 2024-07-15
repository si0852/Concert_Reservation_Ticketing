package com.hhplus.concert_ticketing.util;

import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.util.exception.ExistDataInfoException;
import com.hhplus.concert_ticketing.util.exception.InSufficientBalanceException;
import com.hhplus.concert_ticketing.util.exception.InvalidTokenException;
import com.hhplus.concert_ticketing.util.exception.NoInfoException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ResponseDto> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ResponseDto(500, "Server Error", null));
    }

    @ExceptionHandler(value = InvalidTokenException.class)
    public ResponseEntity<ResponseDto> InvalidException(InvalidTokenException e) {
        return ResponseEntity.status(e.getResponseDto().code())
                .body(new ResponseDto(e.getResponseDto().code(), e.getResponseDto().message(), e.getResponseDto().data()));
    }

    @ExceptionHandler(value = NoInfoException.class)
    public ResponseEntity<ResponseDto> NoInfoException(NoInfoException e) {
        return ResponseEntity.status(e.getResponseDto().code())
                .body(new ResponseDto(e.getResponseDto().code(), e.getResponseDto().message(), e.getResponseDto().data()));
    }

    @ExceptionHandler(value = InSufficientBalanceException.class)
    public ResponseEntity<ResponseDto> InSufficientBalance(InSufficientBalanceException e) {
        return ResponseEntity.status(e.getResponseDto().code())
                .body(new ResponseDto(e.getResponseDto().code(), e.getResponseDto().message(), e.getResponseDto().data()));
    }

    @ExceptionHandler(value = ExistDataInfoException.class)
    public ResponseEntity<ResponseDto> ExistDataInfoException(ExistDataInfoException e) {
        return ResponseEntity.status(e.getResponseDto().code())
                .body(new ResponseDto(e.getResponseDto().code(), e.getResponseDto().message(), e.getResponseDto().data()));
    }
}


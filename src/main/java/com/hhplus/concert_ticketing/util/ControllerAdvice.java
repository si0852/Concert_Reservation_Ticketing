package com.hhplus.concert_ticketing.util;

import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.util.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ResponseDto> handleException(Exception e) {
        log.error("Exception Error : " + e.getMessage());
        return ResponseEntity.status(500).body(new ResponseDto(500, "Server Error", null));
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ResponseDto> handleRuntimeException(Exception e) {
        log.error("handleRuntimeException Error : " + e.getMessage());
        return ResponseEntity.status(500).body(new ResponseDto(500, "Server Error", null));
    }

    @ExceptionHandler(value = InvalidTokenException.class)
    public ResponseEntity<ResponseDto> InvalidException(InvalidTokenException e) {
        log.error("InvalidTokenException Error : " + e.getResponseDto().message());
        return ResponseEntity.status(e.getResponseDto().code())
                .body(new ResponseDto(e.getResponseDto().code(), e.getResponseDto().message(), e.getResponseDto().data()));
    }

    @ExceptionHandler(value = NoInfoException.class)
    public ResponseEntity<ResponseDto> NoInfoException(NoInfoException e) {
        log.error("NoInfoException Error : " + e.getResponseDto().message());
        return ResponseEntity.status(e.getResponseDto().code())
                .body(new ResponseDto(e.getResponseDto().code(), e.getResponseDto().message(), e.getResponseDto().data()));
    }

    @ExceptionHandler(value = InSufficientBalanceException.class)
    public ResponseEntity<ResponseDto> InSufficientBalance(InSufficientBalanceException e) {
        log.error("InSufficientBalanceException Error : " + e.getResponseDto().message());
        return ResponseEntity.status(e.getResponseDto().code())
                .body(new ResponseDto(e.getResponseDto().code(), e.getResponseDto().message(), e.getResponseDto().data()));
    }

    @ExceptionHandler(value = ExistDataInfoException.class)
    public ResponseEntity<ResponseDto> ExistDataInfoException(ExistDataInfoException e) {
        log.error("ExistDataInfoException Error : " + e.getResponseDto().message());
        return ResponseEntity.status(e.getResponseDto().code())
                .body(new ResponseDto(e.getResponseDto().code(), e.getResponseDto().message(), e.getResponseDto().data()));
    }

    @ExceptionHandler(value = LockException.class)
    public ResponseEntity<ResponseDto> LockException(LockException e) {
        log.error("LockException Error : " + e.getResponseDto().message());
        return ResponseEntity.status(e.getResponseDto().code())
                .body(new ResponseDto(e.getResponseDto().code(), e.getResponseDto().message(), e.getResponseDto().data()));
    }
}


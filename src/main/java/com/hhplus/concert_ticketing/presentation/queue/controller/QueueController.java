package com.hhplus.concert_ticketing.presentation.queue.controller;

import com.hhplus.concert_ticketing.application.queue.TokenManagementFacade;
import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.presentation.dto.response.TokenResponse;
import com.hhplus.concert_ticketing.presentation.queue.dto.TokenDto;
import com.hhplus.concert_ticketing.presentation.queue.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
public class QueueController {

    private final TokenManagementFacade tokenManagementFacade;

    // 유저 토큰 발급 API
    // http://localhost:8080/api/token
    @PostMapping("/token")
    @Operation(summary = "유저 토큰 발급 API", description = "대기열을 위한 유저 토큰 발급 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 발급이 되었습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "예약 진행중인 데이터가 존재합니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "userId", description = "유저 id", example = "1")
    })
    public ResponseEntity<?> generateToken(@RequestParam String userId) {
        try {
            log.info("msg : " + userId);
            UserDto userDto = tokenManagementFacade.addToQueue(userId);
            return ResponseEntity.ok(new ResponseDto(HttpServletResponse.SC_OK, "Success", userDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    // 유저 토큰 정보 Return API
    // http://localhost:8080/api/token
    @GetMapping("/info")
    @Operation(summary = "유저 토큰 정보", description = "토큰 상태 확인을 위한 토큰 정보 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 발급이 되었습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "예약 진행중인 데이터가 존재합니다. / 토큰이 만료되었습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "토큰정보가 존재하지 않습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "token", description = "token 값", example = "asleisl293sl")
    })
    public ResponseEntity<ResponseDto> getTokenInfo(@RequestHeader("Authorization")String token) throws Exception {
        TokenDto tokenDto = tokenManagementFacade.getTokenData(token);
        return ResponseEntity.ok(new ResponseDto(HttpServletResponse.SC_OK, "Success", tokenDto));
    }
}

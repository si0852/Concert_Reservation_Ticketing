package com.hhplus.concert_ticketing.presentation;

import com.hhplus.concert_ticketing.presentation.dto.request.BalanceRequest;
import com.hhplus.concert_ticketing.presentation.dto.request.PaymentRequest;
import com.hhplus.concert_ticketing.presentation.dto.request.ReservationRequest;
import com.hhplus.concert_ticketing.presentation.dto.response.*;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class TicketingController {


    // 유저 토큰 발급 API
    // http://localhost:8080/api/token
    @PostMapping("/api/token")
    @Operation(summary = "유저 토큰 발급 API", description = "대기열을 위한 유저 토큰 발급 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 발급이 되었습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "예약 진행중인 데이터가 존재합니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "userId", description = "유저 id", example = "1")
    })
    public ResponseEntity<TokenResponse> generateToken(@RequestParam Long userId) {
        String token = "This_is_Token_mocked_token_for_" + userId; // 임시토큰형식
        TokenStatus status = TokenStatus.ACTIVE;
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5); // 만료시간이 1시간

        TokenResponse response = TokenResponse.builder()
                .token(token)
                .queuePosition(11L)
                .expired_at(expiresAt)
                .build();

        return ResponseEntity.ok(response);
    }

    // 유저 토큰 순번 Return API
    // http://localhost:8080/api/token
    @GetMapping("/api/token")
    @Operation(summary = "유저 토큰 정보", description = "대기열의 토큰 순번 확인을 위한 토큰 정보 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 발급이 되었습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "예약 진행중인 데이터가 존재합니다. / 토큰이 만료되었습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "token", description = "token 값", example = "asleisl293sl")
    })
    public ResponseEntity<TokenResponse> getTokenWithPosition(@RequestParam String token) {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5); // 만료시간이 1시간
        TokenResponse response = TokenResponse.builder()
                .token(token)
                .queuePosition(11L)
                .expired_at(expiresAt)
                .build();

        return ResponseEntity.ok(response);
    }

    // 유저 토큰 순번 Return API
    // http://localhost:8080/api/token
    @GetMapping("/api/tokenInfo")
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
    public ResponseEntity<TokenResponse> getTokenInfo(@RequestParam String token) {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5); // 만료시간이 1시간
        TokenResponse response = TokenResponse.builder()
                .token(token)
                .queuePosition(11L)
                .expired_at(expiresAt)
                .build();

        return ResponseEntity.ok(response);
    }


    // 예약 가능 날짜 조회
    // http://localhost:8080/api/concert/1/available-dates
    @GetMapping("/api/concert/{concertId}/available-dates")
    @ResponseBody
    @Operation(summary = "예약 가능 좌석/날짜 조회 API", description = "예약 가능한 날짜 및 좌석 조회 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "이미 예약진행중인 데이터가 존재합니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "콘서트 정보가 없습니다./ 콘서트 상세 정보가 없습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버에러", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "token", description = "token 값", example = "asleisl293sl"),
            @Parameter(name = "concertId", description = "concertId 값", example = "1")
    })
    public ResponseEntity<List<ConcertDateResponse>> getAvailableDates(
            @PathVariable Long concertId,
            @RequestParam String token
    ) {
        log.info("you entered concertOptionId :: " + concertId);
        List<ConcertDateResponse> dummyDataList = new ArrayList<>();
        // for 문을 이용해 더미 데이터 생성
        for (int i = 1; i <= 10; i++) {
            ConcertDateResponse dummyData = ConcertDateResponse.builder()
                    .concertOptionId((long)i)
                    .concertDate("2024-07-" + String.format("%02d", i))
                    .build();
            dummyDataList.add(dummyData);
        }

        return ResponseEntity.ok(dummyDataList);
    }


    // 예약 가능 좌석 조회
    @GetMapping("/api/concert/{concertOptionId}/available-seats")
    @Operation(summary = "예약 가능 좌석 조회 API", description = "선택한 콘서트 예약 좌석 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "이미 예약진행중인 데이터가 존재합니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "오픈된 콘서트 정보가 없습니다./ 예약가능한 좌석이 없습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버에러", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "token", description = "token 값", example = "asleisl293sl"),
            @Parameter(name = "concertOptionId", description = "concertOptionId 값", example = "1")
    })
    public ResponseEntity<List<SeatResponse>> getAvailableSeats(
            @RequestParam String token,
            @PathVariable Long concertOptionId
    ) {
        log.info("you entered concertOptionId :: " + concertOptionId);
        List<SeatResponse> seatResponses = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            SeatResponse seatResponse = new SeatResponse(
                    (long) i,
                    "Seat " + i,
                    SeatStatus.AVAILABLE
            );
            seatResponses.add(seatResponse);
        }

        return ResponseEntity.ok(seatResponses);
    }


    // 좌석 예약 요청 API
    /*
    http://localhost:8080/api/reserve
    raw 타입 Json으로 꼭!!
    * */
    @PostMapping("/api/reserve")
    @Operation(summary = "좌석 예약 요청 API", description = "선택한 콘서트 좌석 예약 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "예약중인 정보 입니다./이미 예약진행중인 데이터가 존재합니다. / 토큰 정보가 유효하지 않습니다./예약된 좌석입니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "오픈된 콘서트 정보가 없습니다./ 예약가능한 좌석이 없습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버에러", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "token", description = "token 값", example = "asleisl293sl"),
            @Parameter(name = "seatId", description = "seatId 값", example = "1")
    })
    public ResponseEntity<ReservationResponse> reserveSeat(
            @RequestBody ReservationRequest request) {
        ReservationResponse response = ReservationResponse.builder()
                .reservationId(1L)
                .build();

        return ResponseEntity.ok(response);
    }


    // 잔액 충전
    /*
    * http://localhost:8080/api/balance/charge
    * */
    @PatchMapping("/api/balance/charge")
    @Operation(summary = "잔액 충전 API", description = "잔액 충전")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "1000원 이상의 금액을 충전해주세요", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버에러", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "userId", description = "userId", example = "1"),
            @Parameter(name = "amount", description = "충전할 금액", example = "1000")
    })
    public ResponseEntity<BalanceResponse> chargeBalance(@RequestBody BalanceRequest request) {

        return ResponseEntity.ok(BalanceResponse.builder()
                .totalBalance(request.getAmount()+1000)
                .build());
    }

    // 잔액 조회
    // http://localhost:8080/api/balance?userId=1
    @GetMapping("/api/balance")
    @Operation(summary = "잔액 조회 API", description = "잔액 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버에러", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "userId", description = "userId", example = "1"),
    })
    public ResponseEntity<BalanceResponse> getBalance(@RequestParam Long userId) {
        return ResponseEntity.ok(BalanceResponse.builder()
                .totalBalance(10000L)
                .build());
    }//getBalance

    // 결제 API
    @PostMapping("/api/pay")
    @Operation(summary = "결제 API", description = "예약된 좌석 결제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "토큰정보가 없습니다. / 예약시간이 만료되었습니다./ 유저가 존재하지 않습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "예약정보가 없습니다./좌석 정보가 없습니다./콘서트 옵션 데이터가 존재하지 않습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버에러", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "reservationId", description = "reservationId", example = "1"),
            @Parameter(name = "token", description = "token", example = "asdfasdfa123")
    })
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(PaymentResponse.builder()
                .paymentId(1L)
                .build());
    }//processPayment

}//end

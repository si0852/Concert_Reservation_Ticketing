package com.hhplus.concert_ticketing.presentation;

import com.hhplus.concert_ticketing.application.concert.ConcertInfoManagementFacade;
import com.hhplus.concert_ticketing.application.payment.PaymentManagementFacade;
import com.hhplus.concert_ticketing.application.point.ChargeManagementFacade;
import com.hhplus.concert_ticketing.application.queue.StatusManagementFacade;
import com.hhplus.concert_ticketing.application.queue.TokenManagementFacade;
import com.hhplus.concert_ticketing.application.reservation.ReservationManagementFacade;
import com.hhplus.concert_ticketing.domain.payment.entity.Payment;
import com.hhplus.concert_ticketing.domain.point.entity.Customer;
import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.domain.reservation.entity.Reservation;
import com.hhplus.concert_ticketing.presentation.dto.request.BalanceRequest;
import com.hhplus.concert_ticketing.presentation.dto.request.PaymentRequest;
import com.hhplus.concert_ticketing.presentation.dto.request.ReservationRequest;
import com.hhplus.concert_ticketing.presentation.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
public class TicketingController {

    private final ChargeManagementFacade chargeManagementFacade;
    private final ConcertInfoManagementFacade concertInfoManagementFacade;
    private final PaymentManagementFacade paymentManagementFacade;
    private final ReservationManagementFacade reservationManagementFacade;
    private final StatusManagementFacade statusManagementFacade;
    private final TokenManagementFacade tokenManagementFacade;

    public TicketingController(ChargeManagementFacade chargeManagementFacade, ConcertInfoManagementFacade concertInfoManagementFacade, PaymentManagementFacade paymentManagementFacade, ReservationManagementFacade reservationManagementFacade, StatusManagementFacade statusManagementFacade, TokenManagementFacade tokenManagementFacade) {
        this.chargeManagementFacade = chargeManagementFacade;
        this.concertInfoManagementFacade = concertInfoManagementFacade;
        this.paymentManagementFacade = paymentManagementFacade;
        this.reservationManagementFacade = reservationManagementFacade;
        this.statusManagementFacade = statusManagementFacade;
        this.tokenManagementFacade = tokenManagementFacade;
    }



    // 유저 토큰 순번 Return API
    // http://localhost:8080/api/token
    @GetMapping("/api/token/position")
    @Operation(summary = "유저 토큰 정보", description = "대기열의 토큰 순번 확인을 위한 토큰 정보 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 발급이 되었습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "예약 진행중인 데이터가 존재합니다. / 토큰이 만료되었습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "token", description = "token 값", example = "asleisl293sl")
    })
    public ResponseEntity<ResponseDto> getTokenWithPosition(@RequestHeader("Authorization")String token) {
        Integer tokenPosition = tokenManagementFacade.getTokenPosition(token);
        return ResponseEntity.ok(new ResponseDto(HttpServletResponse.SC_OK, "Success", tokenPosition));
    }

    // 유저 토큰 정보 Return API
    // http://localhost:8080/api/token
    @GetMapping("/api/token/info")
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
        Token tokenInfo = tokenManagementFacade.getTokenInfo(token);
        Integer tokenPosition = tokenManagementFacade.getTokenPosition(token);
        TokenResponse res = new TokenResponse(tokenInfo.getToken(), tokenPosition, tokenInfo.getExpiresAt());
        return ResponseEntity.ok(new ResponseDto(HttpServletResponse.SC_OK, "Success", res));
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
    public ResponseEntity<ResponseDto> reserveSeat(
            @RequestBody ReservationRequest request, @RequestHeader("Authorization")String token)  throws Exception{
        Reservation reservation = reservationManagementFacade.reservationProgress(token, request.getSeatId());
        ReservationResponse response = ReservationResponse.builder()
                .reservationId(reservation.getReservationId())
                .build();

        return ResponseEntity.ok(new ResponseDto(HttpServletResponse.SC_OK, "Success", response));
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
    public ResponseEntity<ResponseDto> chargeBalance(@RequestBody BalanceRequest request) throws Exception{
        Customer customer = chargeManagementFacade.chargingPoint(request.getUserId(), request.getAmount());
        return ResponseEntity.ok(new ResponseDto(HttpServletResponse.SC_OK, "Success", BalanceResponse.builder()
                .totalBalance(customer.getBalance())
                .build()));
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
    public ResponseEntity<ResponseDto> getBalance(@RequestParam Long userId) throws Exception {
        Customer customerData = chargeManagementFacade.getCustomerData(userId);
        return ResponseEntity.ok(new ResponseDto(HttpServletResponse.SC_OK, "Success", BalanceResponse.builder()
                .totalBalance(customerData.getBalance())
                .build()));
    }

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
    public ResponseEntity<ResponseDto> processPayment(
            @RequestBody PaymentRequest request, @RequestHeader("Authorization")String token) throws Exception {
        Payment payment = paymentManagementFacade.paymentProgress(request.getReservationId(), token);
        return ResponseEntity.ok(new ResponseDto(HttpServletResponse.SC_OK, "Success", PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .build()));
    }
}
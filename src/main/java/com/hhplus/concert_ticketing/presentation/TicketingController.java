package com.hhplus.concert_ticketing.presentation;

import com.hhplus.concert_ticketing.presentation.dto.request.BalanceRequest;
import com.hhplus.concert_ticketing.presentation.dto.request.PaymentRequest;
import com.hhplus.concert_ticketing.presentation.dto.request.ReservationRequest;
import com.hhplus.concert_ticketing.presentation.dto.response.*;
import com.hhplus.concert_ticketing.presentation.status.SeatStatus;
import com.hhplus.concert_ticketing.presentation.status.TokenStatus;
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
    public ResponseEntity<TokenResponse> generateToken(@RequestParam String userId) {
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


    // 예약 가능 날짜 조회
    // http://localhost:8080/api/concert/1/available-dates
    @GetMapping("/api/concert/{concertId}/available-dates")
    @ResponseBody
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
    // /api/concert/1/available-seats?date=2024-07-05
    @GetMapping("/api/concert/{concertOptionId}/available-seats")
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
    {
    "userId": 12345,
    "seatId": 67890,
    "status": "WAITING"
    }
    raw 타입 Json으로 꼭!!
    * */
    @PostMapping("/api/reserve")
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
    public ResponseEntity<BalanceResponse> chargeBalance(@RequestBody BalanceRequest request) {

        return ResponseEntity.ok(BalanceResponse.builder()
                .totalBalance(request.getAmount()+1000)
                .build());
    }

    // 잔액 조회
    // http://localhost:8080/api/balance?userId=1
    @GetMapping("/api/balance")
    public ResponseEntity<BalanceResponse> getBalance(@RequestParam Long userId) {
        return ResponseEntity.ok(BalanceResponse.builder()
                .totalBalance(10000L)
                .build());
    }//getBalance

    // 결제 API
    @PostMapping("/api/pay")
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(PaymentResponse.builder()
                .paymentId(1L)
                .build());
    }//processPayment

}//end

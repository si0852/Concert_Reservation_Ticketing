package com.hhplus.concert_ticketing.application.facade.impl;

import com.hhplus.concert_ticketing.business.entity.*;
import com.hhplus.concert_ticketing.application.facade.PaymentManagementFacade;
import com.hhplus.concert_ticketing.business.service.*;
import com.hhplus.concert_ticketing.presentation.dto.response.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class PaymentManagementFacadeImpl implements PaymentManagementFacade {

    private final TokenQueueService tokenQueueService;
    private final SeatService seatService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final CustomerService customerService;
    private final ConcertOptionService concertOptionService;

    public PaymentManagementFacadeImpl(TokenQueueService tokenQueueService, SeatService seatService, ReservationService reservationService, PaymentService paymentService, CustomerService customerService, ConcertOptionService concertOptionService) {
        this.tokenQueueService = tokenQueueService;
        this.seatService = seatService;
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.customerService = customerService;
        this.concertOptionService = concertOptionService;
    }

    @Override
    public Payment paymentProgress(Long reservationId, String token) {
        // 예약 상태 확인
        Reservation validationReservationInfo = reservationService.getReservationDataByReservationId(reservationId);
        // 예약정보가 없다면
        if(validationReservationInfo == null) throw new RuntimeException("예약정보가 없습니다.");

        String reservationStatus = validationReservationInfo.getStatus();
        LocalDateTime createdAt = validationReservationInfo.getCreatedAt();
        LocalDateTime expiredAt = validationReservationInfo.getUpdatedAt();
        Long seconds = Duration.between(createdAt, expiredAt).getSeconds();

        Seat seatData = seatService.getSeatOnlyData(validationReservationInfo.getSeatId());
        // 좌석정보가 없다면
        if(seatData == null) throw new RuntimeException("좌석 정보가 없습니다.");

        Token validateToken = tokenQueueService.validateTokenByToken(token);
        // 토큰정보가 없다면
        if(validateToken == null) throw new RuntimeException("토큰 정보가 없습니다.");

        // -> 토큰 만료
        if (!reservationStatus.equals(ReservationStatus.WAITING.toString())) throw new RuntimeException("토큰이 만료되었습니다.");
        // --> 예약 상태 만료
        if(reservationStatus.equals(ReservationStatus.WAITING.toString()) && seconds > 300L) {
            // ---> 예약 상태 취소로 변경
            validationReservationInfo.setStatus(ReservationStatus.CANCELLED.toString());
            // ---> 토큰 상태 만료로 변경
            validateToken.setStatus(TokenStatus.EXPIRED.toString());
            // ---> 좌석 상태 열림으로 변경
            seatData.setSeatStatus(SeatStatus.AVAILABLE.toString());

            // ----> 예약 상태 취소 처리
            reservationService.UpdateReservationData(validationReservationInfo);
            // ----> 좌석 상태 열림 처리
            seatService.updateSeatData(seatData);
            // ----> 토큰 상태 만료 처리
            tokenQueueService.updateToken(validateToken);
            // -----> 예약 만료 에러 발생
            throw new RuntimeException("예약 시간이 만료되었습니다.");
        }

        // -> 토큰 만료 X
        // --> 콘서트 옵션 데이터 조회
        ConcertOption concertOptionData = concertOptionService.getConcertOptionDataById(seatData.getConcertOptionId());
        if(concertOptionData == null) throw new RuntimeException("콘서트 옵션 데이터가 존재하지 않습니다.");

        // --> 유저 정보 조회
        Customer customerData = customerService.getCustomerData(validateToken.getUserId());
        if(customerData == null) throw new RuntimeException("유저가 존재하지 않습니다.");

        // --> 잔액 유효성 검증
        Double userPoint = customerData.getBalance();
        Double ticketPrice = concertOptionData.getPrice();
        // --> 잔액 부족할 경우
        if (ticketPrice > userPoint) {
            throw new RuntimeException("잔액이 부족합니다.");
        }

        // --> 잔액이 부족하지 않을 경우
        Double totalPoint = userPoint - ticketPrice;
        // ---> 잔액 차감 처리
        customerData.setBalance(totalPoint);
        customerService.updateCharge(customerData);

        // ---> 좌석예약 확정 처리
        seatData.setSeatStatus(SeatStatus.PAID.toString());
        seatService.updateSeatData(seatData);

        // ---> 결재
        Payment payment = new Payment(reservationId, ticketPrice, LocalDateTime.now());
        Payment paymentResult = paymentService.savePayment(payment);

        // ---> 예약
        validationReservationInfo.setStatus(ReservationStatus.PAID.toString());
        reservationService.UpdateReservationData(validationReservationInfo);

        // ---> 토큰 만료
        validateToken.setStatus(TokenStatus.EXPIRED.toString());
        tokenQueueService.updateToken(validateToken);

        return paymentResult;
    }
}

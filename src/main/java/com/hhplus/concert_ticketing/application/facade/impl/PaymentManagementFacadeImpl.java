package com.hhplus.concert_ticketing.application.facade.impl;

import com.hhplus.concert_ticketing.business.entity.*;
import com.hhplus.concert_ticketing.application.facade.PaymentManagementFacade;
import com.hhplus.concert_ticketing.business.service.*;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.status.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import com.hhplus.concert_ticketing.util.exception.InSufficientBalanceException;
import com.hhplus.concert_ticketing.util.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class PaymentManagementFacadeImpl implements PaymentManagementFacade {

    private final TokenService tokenService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final CustomerService customerService;
    private final ConcertService concertService;

    public PaymentManagementFacadeImpl(TokenService tokenService, ReservationService reservationService, PaymentService paymentService, CustomerService customerService, ConcertService concertService) {
        this.tokenService = tokenService;
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.customerService = customerService;
        this.concertService = concertService;
    }

    @Transactional
    @Override
    public Payment paymentProgress(Long reservationId, String token)  throws Exception{
        // 예약 상태 확인
        Reservation validationReservationInfo = reservationService.getReservationDataByReservationId(reservationId);

        String reservationStatus = validationReservationInfo.getStatus();
        LocalDateTime createdAt = validationReservationInfo.getCreatedAt();
        LocalDateTime expiredAt = validationReservationInfo.getUpdatedAt();
        Long seconds = Duration.between(createdAt, expiredAt).getSeconds();

        Seat seatData = concertService.getSeatOnlyData(validationReservationInfo.getSeatId());

        Token validateToken = tokenService.validateTokenByToken(token);

        // -> 토큰 만료 (예약 시간 만료)
        if (reservationStatus.equals(ReservationStatus.PAID.toString())) throw new InvalidTokenException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN,"이미 예약된 정보입니다.", null));
        // --> 예약 상태 만료
        if(reservationStatus.equals(ReservationStatus.WAITING.toString()) && seconds > 300L) {
            // ---> 예약 상태 취소로 변경
            validationReservationInfo.changeStateCancel();
            // ---> 토큰 상태 만료로 변경
            validateToken.changeExpired();
            // ---> 좌석 상태 열림으로 변경
            seatData.changeStateUnlock();

            // ----> 예약 상태 취소 처리
            reservationService.UpdateReservationData(validationReservationInfo);
            // ----> 좌석 상태 열림 처리
            concertService.updateSeatData(seatData);
            // ----> 토큰 상태 만료 처리
            tokenService.updateToken(validateToken);
            // -----> 예약 만료 에러 발생
            throw new InvalidTokenException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN,"예약 시간이 만료되었습니다.", null));
        }

        // -> 토큰 만료 X
        // --> 콘서트 옵션 데이터 조회
        ConcertOption concertOptionData = concertService.getConcertOptionDataById(seatData.getConcertOptionId());

        // --> 유저 정보 조회
        Customer customerData = customerService.getCustomerData(validateToken.getUserId());

        // --> 잔액 유효성 검증
//        Double userPoint = customerData.getBalance();
        Double ticketPrice = concertOptionData.getPrice();
        // --> 잔액 부족할 경우
//        if (ticketPrice > userPoint) {
//            throw new InSufficientBalanceException(new ResponseDto(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"잔액이 부족합니다.", userPoint));
//        }

        // --> 잔액이 부족하지 않을 경우
//        Double totalPoint = userPoint - ticketPrice;
        // ---> 잔액 차감 처리
        customerData.subtractBalance(ticketPrice);
        customerService.updateCharge(customerData);

        // ---> 좌석예약 확정 처리
        seatData.changeStatePaid();
        concertService.updateSeatData(seatData);

        // ---> 결재
        Payment payment = new Payment(reservationId, ticketPrice, LocalDateTime.now());
        Payment paymentResult = paymentService.savePayment(payment);

        // ---> 예약
        validationReservationInfo.changeStatePaid();
        reservationService.UpdateReservationData(validationReservationInfo);

        // ---> 토큰 만료
        validateToken.changeExpired();
        tokenService.updateToken(validateToken);

        return paymentResult;
    }
}

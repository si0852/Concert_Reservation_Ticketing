package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Reservation;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.facade.ReservationManagementFacade;
import com.hhplus.concert_ticketing.business.service.ReservationService;
import com.hhplus.concert_ticketing.business.service.SeatService;
import com.hhplus.concert_ticketing.business.service.TokenQueueService;
import com.hhplus.concert_ticketing.presentation.dto.response.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationManagementFacadeImpl implements ReservationManagementFacade {

    private final TokenQueueService tokenQueueService;
    private final ReservationService reservationService;
    private final SeatService seatService;

    public ReservationManagementFacadeImpl(TokenQueueService tokenQueueService, ReservationService reservationService, SeatService seatService) {
        this.tokenQueueService = tokenQueueService;
        this.reservationService = reservationService;
        this.seatService = seatService;
    }

    @Transactional
    @Override
    public Reservation reservationProgress(String tokenData, Long seatId) {
        // 토큰 확인
        Token token = tokenQueueService.validateTokenByToken(tokenData);
        // 토큰 상태가 ACTIVE이면 RuntimeException
        if(token != null && !token.getStatus().equals(TokenStatus.ACTIVE.toString())) throw new RuntimeException("토큰 정보가 유효하지 않습니다.");

        // user 1명당 예약은 1명 제한을 두었다. 해당 케이스 validation check
        List<Reservation> reservationDataByUserId = reservationService.getReservationDataByUserId(token.getUserId());
        List<Reservation> filterOutCancel = reservationDataByUserId.stream().filter(data -> data.getStatus().equals(ReservationStatus.CANCELLED.toString())).toList();

        if(filterOutCancel.size() > 0) throw new RuntimeException("이미 예약된 데이터가 존재합니다.");

        // 예약 정보 확인
        Reservation reservationData = reservationService.getReservationData(token.getUserId(), seatId);
        // 예약 정보 상태가 취소가 아니면(예약중이거나 결제 이면) RuntimeException
        String status = reservationData.getStatus();
        if(reservationData != null &&
                (status.equals(ReservationStatus.PAID.toString()) || status.equals(ReservationStatus.WAITING.toString()))) throw new RuntimeException("예약중인 정보입니다.");

        // 예약 정보 상태가 취소이면 상태값 변경
        if(reservationData != null && reservationData.getStatus().equals(ReservationStatus.CANCELLED.toString())) {
            reservationData.setStatus(ReservationStatus.WAITING.toString());
        // 예약 정보가 없으면 예약정보 생성
        } else if (reservationData == null) {
            LocalDateTime now = token.getCreatedAt();
            reservationData = new Reservation(token.getUserId(), seatId, ReservationStatus.WAITING.toString(), now, now.plusMinutes(5));
        }
        // 추가 또는 업데이트
        Reservation saveReservation = reservationService.SaveReservationData(reservationData);


        // 좌석정보 확인
        Seat seatOnlyData = seatService.getSeatOnlyData(seatId);
        // 좌석정보가 이미 예약이 되어 있으면 RuntimeException
        if(seatOnlyData.getSeatStatus().equals(SeatStatus.RESERVED.toString())) throw new RuntimeException("예약된 좌석입니다.");
        // 좌석정보가 예약이 아니면 예약상태로 변경
        seatOnlyData.setSeatStatus(SeatStatus.RESERVED.toString());

        // 좌석정보 업데이트
        seatService.updateSeatData(seatOnlyData);

        return saveReservation;
    }
}

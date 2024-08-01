package com.hhplus.concert_ticketing.application.facade.impl;

import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.entity.Reservation;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.application.facade.ReservationManagementFacade;
import com.hhplus.concert_ticketing.business.service.ConcertService;
import com.hhplus.concert_ticketing.business.service.ReservationService;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.status.ReservationStatus;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.util.exception.ExistDataInfoException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationManagementFacadeImpl implements ReservationManagementFacade {

    private final TokenService tokenService;
    private final ReservationService reservationService;
    private final ConcertService concertService;

    public ReservationManagementFacadeImpl(TokenService tokenService, ReservationService reservationService, ConcertService concertService) {
        this.tokenService = tokenService;
        this.reservationService = reservationService;
        this.concertService = concertService;
    }

    @Transactional
    @Override
    public Reservation reservationProgress(String tokenData, Long seatId) throws Exception {
        // 토큰 확인
        Token token = tokenService.validateTokenByToken(tokenData);
        // 좌석정보 확인
        Seat seatOnlyData = concertService.getSeatOnlyData(seatId);
        // 좌석정보가 이미 예약이 되어 있으면 RuntimeException
        if(!seatOnlyData.getSeatStatus().equals(SeatStatus.AVAILABLE.toString())) {
            throw new ExistDataInfoException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN, "예약된 좌석입니다.", SeatStatus.RESERVED.toString()));}

        ConcertOption concertOptionData = concertService.getConcertOptionDataById(seatOnlyData.getConcertOptionId());
        concertService.getConcertData(concertOptionData.getConcertId());

        // user 1명당 예약은 1명 제한을 두었다. 해당 케이스 validation check
        List<Reservation> reservationDataByUserId = reservationService.getReservationDataByUserId(token.getUserId());
        List<Reservation> filterOutCancel = reservationDataByUserId.stream().filter(data -> data.getStatus().equals(ReservationStatus.CANCELLED.toString())).toList();

        if(filterOutCancel.size() > 0) throw new ExistDataInfoException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN, "이미 예약된 데이터가 존재합니다.", SeatStatus.RESERVED.toString()));

        // 예약 정보 확인
        Reservation reservationData = reservationService.getReservationData(token.getUserId(), seatId);

        if (reservationData != null) {
            String status = reservationData.getStatus();
            // 예약 정보 상태가 취소가 아니면(예약중이거나 결제 이면) RuntimeException
            if (status.equals(ReservationStatus.PAID.toString()))throw new ExistDataInfoException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN, "이미 예약중인 정보입니다.", SeatStatus.RESERVED.toString()));
            // 예약 정보 상태가 취소이면 상태값 변경
            else if(reservationData.getStatus().equals(ReservationStatus.CANCELLED.toString())){
                reservationData.changeStateReservation();
            }
        }
        if (reservationData == null) {
            LocalDateTime now = token.getCreatedAt();
            reservationData = new Reservation(token.getUserId(), seatId, ReservationStatus.WAITING.toString(), now, now.plusMinutes(5));
        }
        // 추가 또는 업데이트
        Reservation saveReservation = reservationService.SaveReservationData(reservationData);

        // 좌석정보가 예약이 아니면 예약상태로 변경
        seatOnlyData.changeStateReserve();

        // 좌석정보 업데이트
        concertService.updateSeatData(seatOnlyData);

        return saveReservation;
    }
}

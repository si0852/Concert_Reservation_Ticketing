package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.facade.SeatManagementFacade;
import com.hhplus.concert_ticketing.business.service.ConcertOptionService;
import com.hhplus.concert_ticketing.business.service.SeatService;
import com.hhplus.concert_ticketing.business.service.TokenQueueService;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SeatManagementFacadeIml implements SeatManagementFacade {

    private static final Logger log = LoggerFactory.getLogger(SeatManagementFacadeIml.class);
    private final TokenQueueService tokenQueueService;
    private final SeatService seatService;
    private final ConcertOptionService concertOptionService;


    public SeatManagementFacadeIml(TokenQueueService tokenQueueService, SeatService seatService, ConcertOptionService concertOptionService) {
        this.tokenQueueService = tokenQueueService;
        this.seatService = seatService;
        this.concertOptionService = concertOptionService;
    }

    @Override
    public List<Seat> getSeatData(Long concertOptionId, String tokend) {

        Token token = tokenQueueService.validateTokenByToken(tokend);
        if(token != null && !token.getStatus().equals(TokenStatus.ACTIVE.toString())) throw new RuntimeException("이미 예약진행중인 데이터가 존재합니다.");

        ConcertOption concertOptionData = concertOptionService.getConcertOptionDataByLocalDate(concertOptionId);
        if(concertOptionData == null) throw new RuntimeException("오픈된 콘서트 정보가 없습니다.");

        String status = SeatStatus.AVAILABLE.toString();
        List<Seat> seatData = seatService.getSeatData(concertOptionId, status);
        if(seatData.size() == 0) throw new RuntimeException("예약가능한 좌석이 없습니다.");
        return seatData;
    }
}

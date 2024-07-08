package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.facade.SeatManagementFacade;
import com.hhplus.concert_ticketing.business.service.ConcertOptionService;
import com.hhplus.concert_ticketing.business.service.SeatService;
import com.hhplus.concert_ticketing.status.SeatStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SeatManagementFacadeIml implements SeatManagementFacade {

    private static final Logger log = LoggerFactory.getLogger(SeatManagementFacadeIml.class);
    private final SeatService seatService;
    private final ConcertOptionService concertOptionService;


    public SeatManagementFacadeIml(SeatService seatService, ConcertOptionService concertOptionService) {
        this.seatService = seatService;
        this.concertOptionService = concertOptionService;
    }

    @Override
    public List<Seat> getSeatData(Long concertOptionId, Long tokenId) {
        ConcertOption concertOptionData = concertOptionService.getConcertOptionDataByLocalDate(concertOptionId);
        if(concertOptionData == null) throw new RuntimeException("오픈된 콘서트 정보가 없습니다.");

        String status = SeatStatus.AVAILABLE.toString();
        List<Seat> seatData = seatService.getSeatData(concertOptionId, status);
        if(seatData.size() == 0) throw new RuntimeException("예약가능한 좌석이 없습니다.");
        return seatData;
    }
}

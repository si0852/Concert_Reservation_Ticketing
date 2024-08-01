package com.hhplus.concert_ticketing.application.facade.impl;

import com.hhplus.concert_ticketing.domain.concert.entity.Concert;
import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import com.hhplus.concert_ticketing.application.facade.ConcertInfoManagementFacade;
import com.hhplus.concert_ticketing.domain.concert.service.ConcertService;
import com.hhplus.concert_ticketing.domain.queue.service.TokenService;
import com.hhplus.concert_ticketing.status.SeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ConcertInfoManagementFacadeImpl implements ConcertInfoManagementFacade {


    private final TokenService tokenService;
    private final ConcertService concertService;

    public ConcertInfoManagementFacadeImpl(TokenService tokenService, ConcertService concertService) {
        this.tokenService = tokenService;
        this.concertService = concertService;
    }

    @Transactional
    @Override
    public List<ConcertOption> getConcertOption(String tokendata, Long concertId) throws Exception {
//        tokenService.validateTokenByToken(tokendata);

        concertService.getConcertData(concertId);

        List<ConcertOption> concertOptionData = concertService.getConcertOptionData(concertId);

        return concertOptionData;
    }

    @Transactional
    @Override
    public List<Seat> getSeatData(Long concertOptionId, String tokend)  throws Exception{

//        tokenService.validateTokenByToken(tokend);
        concertService.getConcertOptionDataByLocalDate(concertOptionId);

        String status = SeatStatus.AVAILABLE.toString();
        List<Seat> seatData = concertService.getSeatData(concertOptionId, status);
        return seatData;
    }

    @Override
    public List<Concert> getConcertData() throws Exception {
        return concertService.getConcertData();
    }
}

package com.hhplus.concert_ticketing.application.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Concert;
import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.application.facade.ConcertInfoManagementFacade;
import com.hhplus.concert_ticketing.business.service.ConcertService;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
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
        tokenService.validateTokenByToken(tokendata);

        concertService.getConcertData(concertId);

        List<ConcertOption> concertOptionData = concertService.getConcertOptionData(concertId);

        return concertOptionData;
    }

    @Transactional
    @Override
    public List<Seat> getSeatData(Long concertOptionId, String tokend)  throws Exception{

        tokenService.validateTokenByToken(tokend);
        concertService.getConcertOptionDataByLocalDate(concertOptionId);

        String status = SeatStatus.AVAILABLE.toString();
        List<Seat> seatData = concertService.getSeatData(concertOptionId, status);
        return seatData;
    }
}

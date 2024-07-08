package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Concert;
import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.facade.ConcertInfoManagementFacade;
import com.hhplus.concert_ticketing.business.service.ConcertOptionService;
import com.hhplus.concert_ticketing.business.service.ConcertService;
import com.hhplus.concert_ticketing.business.service.TokenQueueService;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ConcertInfoManagementFacadeImpl implements ConcertInfoManagementFacade {

    private final TokenQueueService tokenQueueService;
    private final ConcertOptionService concertOptionService;
    private final ConcertService concertService;

    public ConcertInfoManagementFacadeImpl(TokenQueueService tokenQueueService, ConcertOptionService concertOptionService, ConcertService concertService) {
        this.tokenQueueService = tokenQueueService;
        this.concertOptionService = concertOptionService;
        this.concertService = concertService;
    }

    @Transactional
    @Override
    public List<ConcertOption> getConcertOption(Long userId, Long concertId) {
        Token token = tokenQueueService.validateToken(userId);
        if(token != null && (token.getStatus().equals(TokenStatus.ACTIVE.toString()))) throw new RuntimeException("이미 예약중인 데이터가 존재합니다.");
        if(token != null && (token.getStatus().equals(TokenStatus.WAITING.toString()))) throw new RuntimeException("이미 진행중인 데이터가 존재합니다.");

        Concert concertData = concertService.getConcertData(concertId);
        if (concertData == null) throw new RuntimeException("콘서트 정보가 없습니다.");

        List<ConcertOption> concertOptionData = concertOptionService.getConcertOptionData(concertId);
        if(concertOptionData.size() == 0) throw new RuntimeException("콘서트 정보가 없습니다.");

        return concertOptionData;
    }
}

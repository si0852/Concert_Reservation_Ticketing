package com.hhplus.concert_ticketing.application.concert.impl;

import com.hhplus.concert_ticketing.application.concert.ConcertInfoManagementFacade;
import com.hhplus.concert_ticketing.domain.concert.entity.Concert;
import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import com.hhplus.concert_ticketing.domain.concert.mapper.ConcertMapper;
import com.hhplus.concert_ticketing.domain.concert.service.ConcertService;
import com.hhplus.concert_ticketing.domain.queue.service.TokenService;
import com.hhplus.concert_ticketing.presentation.concert.dto.ConcertDto;
import com.hhplus.concert_ticketing.presentation.concert.dto.ConcertOptionDto;
import com.hhplus.concert_ticketing.presentation.concert.dto.SeatDto;
import com.hhplus.concert_ticketing.status.SeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConcertInfoManagementFacadeImpl implements ConcertInfoManagementFacade {


    private final TokenService tokenService;
    private final ConcertService concertService;

    public ConcertInfoManagementFacadeImpl(TokenService tokenService, ConcertService concertService) {
        this.tokenService = tokenService;
        this.concertService = concertService;
    }

    @Override
    public List<ConcertOptionDto> getConcertOption(Long concertId) throws Exception {
        concertService.getConcertData(concertId);

        List<ConcertOption> concertOptionData = concertService.getConcertOptionData(concertId);

        return concertOptionData.stream()
                .map(ConcertMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatDto> getSeatData(Long concertOptionId)  throws Exception{
        concertService.getConcertOptionData(concertOptionId);

        String status = SeatStatus.AVAILABLE.toString();
        List<Seat> seatData = concertService.getSeatData(concertOptionId, status);
        return seatData.stream()
                .map(ConcertMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConcertDto> getConcertData() throws Exception {
        try {
            List<Concert> concertDataList = concertService.getConcertData();
            return concertDataList.stream()
                    .map(ConcertMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw e;
        }
    }
}

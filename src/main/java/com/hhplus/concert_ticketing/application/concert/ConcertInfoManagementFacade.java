package com.hhplus.concert_ticketing.application.concert;

import com.hhplus.concert_ticketing.presentation.concert.dto.ConcertDto;
import com.hhplus.concert_ticketing.presentation.concert.dto.ConcertOptionDto;
import com.hhplus.concert_ticketing.presentation.concert.dto.SeatDto;

import java.util.List;

public interface ConcertInfoManagementFacade {

    List<ConcertOptionDto> getConcertOption(Long concertId) throws Exception;

    List<SeatDto> getSeatData(Long concertOptionId) throws Exception;

    List<ConcertDto> getConcertData() throws Exception;
}

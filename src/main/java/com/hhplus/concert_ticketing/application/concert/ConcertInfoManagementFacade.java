package com.hhplus.concert_ticketing.application.concert;

import com.hhplus.concert_ticketing.domain.concert.entity.Concert;
import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;

import java.util.List;

public interface ConcertInfoManagementFacade {

    List<ConcertOption> getConcertOption(String token, Long concertId) throws Exception;

    List<Seat> getSeatData(Long concertOptionId, String token) throws Exception;

    List<Concert> getConcertData() throws Exception;
}

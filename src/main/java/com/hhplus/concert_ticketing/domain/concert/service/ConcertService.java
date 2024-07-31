package com.hhplus.concert_ticketing.domain.concert.service;

import com.hhplus.concert_ticketing.domain.concert.entity.Concert;
import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertService {

    Concert saveConcertData(Concert concert);

    Concert updateConcertData(Concert concert);

    Concert getConcertData(Long concertId);

    ConcertOption saveConcertOption(ConcertOption concertOption);

    ConcertOption updateConcertOption(ConcertOption concertOption);

    List<ConcertOption> getConcertOptionData(Long concertId);

    List<Concert> getConcertData();

    ConcertOption getConcertOptionDataByLocalDate(Long concertOptionId);

    ConcertOption getConcertOptionDataById(Long concertOptionid);

    List<LocalDateTime> getConcertDate(Long concertOptionId);

    Seat saveSeatData(Seat seat);

    List<Seat> getSeatData(Long concertOptionId, String status);

    Seat getSeatOnlyData(Long seatId);

    Seat updateSeatData(Seat seat);
}

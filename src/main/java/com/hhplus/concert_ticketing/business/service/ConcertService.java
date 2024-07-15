package com.hhplus.concert_ticketing.business.service;

import com.hhplus.concert_ticketing.business.entity.Concert;
import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.entity.Seat;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertService {

    public Concert saveConcertData(Concert concert);

    public Concert getConcertData(Long concertId);

    ConcertOption saveConcertOption(ConcertOption concertOption);

    ConcertOption updateConcertOption(ConcertOption concertOption);

    List<ConcertOption> getConcertOptionData(Long concertId);

    ConcertOption getConcertOptionDataByLocalDate(Long concertOptionId);

    ConcertOption getConcertOptionDataById(Long concertOptionid);

    List<LocalDateTime> getConcertDate(Long concertOptionId);

    Seat saveSeatData(Seat seat);

    List<Seat> getSeatData(Long concertOptionId, String status);

    Seat getSeatOnlyData(Long seatId);

    Seat updateSeatData(Seat seat);
}

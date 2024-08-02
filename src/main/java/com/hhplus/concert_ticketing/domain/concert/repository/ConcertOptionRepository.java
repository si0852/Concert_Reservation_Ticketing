package com.hhplus.concert_ticketing.domain.concert.repository;

import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertOptionRepository {

    ConcertOption saveData(ConcertOption concertOption);

    ConcertOption update(ConcertOption concertOption);

    ConcertOption getConcertOptionDataByLocalDate(Long concertOptionId);

    ConcertOption getConcertOptionDataById(Long concertOptionId);

    List<ConcertOption> getConcertOptionData(Long concertId);

    List<ConcertOption> getConcertOptionDataByConcertOptionId(Long concertOptionId);

    List<LocalDateTime> getConcertDate(Long concertId);


}

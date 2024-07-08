package com.hhplus.concert_ticketing.business.repository;

import com.hhplus.concert_ticketing.business.entity.ConcertOption;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertOptionRepository {

    ConcertOption saveData(ConcertOption concertOption);

    ConcertOption update(ConcertOption concertOption);

    ConcertOption getConcertOptionDataByLocalDate(Long concertOptionId);

    List<ConcertOption> getConcertOptionData(Long concertId);

    List<LocalDateTime> getConcertDate(Long concertId);


}

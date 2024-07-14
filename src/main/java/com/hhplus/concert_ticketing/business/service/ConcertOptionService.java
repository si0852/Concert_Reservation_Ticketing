package com.hhplus.concert_ticketing.business.service;

import com.hhplus.concert_ticketing.business.entity.ConcertOption;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertOptionService {

    ConcertOption saveConcertOption(ConcertOption concertOption);

    ConcertOption updateConcertOption(ConcertOption concertOption);

    List<ConcertOption> getConcertOptionData(Long concertId);

    ConcertOption getConcertOptionDataByLocalDate(Long concertOptionId);

    ConcertOption getConcertOptionDataById(Long concertOptionid);

    List<LocalDateTime> getConcertDate(Long concertOptionId);
}

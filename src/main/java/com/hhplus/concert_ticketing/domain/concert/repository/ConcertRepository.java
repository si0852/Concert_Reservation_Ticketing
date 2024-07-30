package com.hhplus.concert_ticketing.domain.concert.repository;

import com.hhplus.concert_ticketing.domain.concert.entity.Concert;

import java.util.List;

public interface ConcertRepository {

    Concert saveData(Concert concert);

    Concert getConcertData(Long concertId);

    List<Concert> getConcertData();
}

package com.hhplus.concert_ticketing.business.repository;

import com.hhplus.concert_ticketing.business.entity.Concert;

import java.util.List;

public interface ConcertRepository {

    Concert saveData(Concert concert);

    Concert getConcertData(Long concertId);

    List<Concert> getConcertData();
}

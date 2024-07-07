package com.hhplus.concert_ticketing.business.service;

import com.hhplus.concert_ticketing.business.entity.Concert;

public interface ConcertService {

    public Concert saveConcertData(Concert concert);

    public Concert getConcertData(Long concertId);
}

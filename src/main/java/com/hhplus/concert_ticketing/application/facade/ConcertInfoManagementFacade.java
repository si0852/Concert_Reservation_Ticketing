package com.hhplus.concert_ticketing.application.facade;

import com.hhplus.concert_ticketing.business.entity.ConcertOption;

import java.util.List;

public interface ConcertInfoManagementFacade {

    List<ConcertOption> getConcertOption(String token, Long concertId);
}

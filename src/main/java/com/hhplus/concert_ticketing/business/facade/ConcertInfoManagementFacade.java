package com.hhplus.concert_ticketing.business.facade;

import com.hhplus.concert_ticketing.business.entity.ConcertOption;

import java.util.List;

public interface ConcertInfoManagementFacade {

    List<ConcertOption> getConcertOption(Long tokenId, Long concertId);
}

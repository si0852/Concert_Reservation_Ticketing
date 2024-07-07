package com.hhplus.concert_ticketing.business.service.impl;

import com.hhplus.concert_ticketing.business.entity.Concert;
import com.hhplus.concert_ticketing.business.repository.ConcertRepository;
import com.hhplus.concert_ticketing.business.service.ConcertService;
import org.springframework.stereotype.Service;

@Service
public class ConcertServiceImpl implements ConcertService {

    private final ConcertRepository concertRepository;

    public ConcertServiceImpl(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    @Override
    public Concert saveConcertData(Concert concert) {
        return concertRepository.saveData(concert);
    }

    @Override
    public Concert getConcertData(Long concertId) {
        return concertRepository.getConcertData(concertId);
    }
}

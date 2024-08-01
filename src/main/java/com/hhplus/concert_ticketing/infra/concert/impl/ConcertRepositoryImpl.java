package com.hhplus.concert_ticketing.infra.concert.impl;

import com.hhplus.concert_ticketing.domain.concert.entity.Concert;
import com.hhplus.concert_ticketing.domain.concert.repository.ConcertRepository;
import com.hhplus.concert_ticketing.infra.concert.JpaConcertRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConcertRepositoryImpl implements ConcertRepository {

    private final JpaConcertRepository jpaConcertRepository;

    public ConcertRepositoryImpl(JpaConcertRepository jpaConcertRepository) {
        this.jpaConcertRepository = jpaConcertRepository;
    }

    @Override
    public Concert saveData(Concert concert) {
        return jpaConcertRepository.save(concert);
    }

    @Override
    public Concert getConcertData(Long concertId) {
        return jpaConcertRepository.findById(concertId).orElse(null);
    }

    @Override
    public List<Concert> getConcertData() {
        return jpaConcertRepository.findAll();
    }
}

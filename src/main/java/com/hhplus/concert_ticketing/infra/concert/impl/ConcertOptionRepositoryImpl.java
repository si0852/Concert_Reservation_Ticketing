package com.hhplus.concert_ticketing.infra.concert.impl;

import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.repository.ConcertOptionRepository;
import com.hhplus.concert_ticketing.infra.concert.JpaConcertOptionRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ConcertOptionRepositoryImpl implements ConcertOptionRepository {

    private final JpaConcertOptionRepository jpaConcertOptionRepository;

    public ConcertOptionRepositoryImpl(JpaConcertOptionRepository jpaConcertOptionRepository) {
        this.jpaConcertOptionRepository = jpaConcertOptionRepository;
    }

    @Override
    public ConcertOption saveData(ConcertOption concertOption) {
        return jpaConcertOptionRepository.save(concertOption);
    }

    @Override
    public ConcertOption update(ConcertOption concertOption) {
        return jpaConcertOptionRepository.save(concertOption);
    }

    @Override
    public List<ConcertOption> getConcertOptionData(Long concertId) {
        return jpaConcertOptionRepository.findAllByConcertId(concertId);
    }

    @Override
    public List<ConcertOption> getConcertOptionDataByConcertOptionId(Long concertOptionId) {
        return jpaConcertOptionRepository.findAllByConcertOptionId(concertOptionId);
    }

    @Override
    public ConcertOption getConcertOptionDataByLocalDate(Long concertOptionId) {
        return jpaConcertOptionRepository.findByConcertOptionIdAndConcertDateGreaterThanEqual(concertOptionId, LocalDateTime.now()).orElse(null);
    }

    @Override
    public ConcertOption getConcertOptionDataById(Long concertOptionId) {
        return jpaConcertOptionRepository.findById(concertOptionId).orElse(null);
    }

    @Override
    public List<LocalDateTime> getConcertDate(Long concertId) {
        return jpaConcertOptionRepository.findConcertDateByConcertId(concertId);
    }



}

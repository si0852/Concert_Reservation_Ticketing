package com.hhplus.concert_ticketing.business.service.impl;

import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.repository.ConcertOptionRepository;
import com.hhplus.concert_ticketing.business.service.ConcertOptionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConcertOptionServiceImpl implements ConcertOptionService {

    private final ConcertOptionRepository concertOptionRepository;

    public ConcertOptionServiceImpl(ConcertOptionRepository concertOptionRepository) {
        this.concertOptionRepository = concertOptionRepository;
    }

    @Override
    public ConcertOption saveConcertOption(ConcertOption concertOption) {
        return concertOptionRepository.saveData(concertOption);
    }

    @Override
    public ConcertOption updateConcertOption(ConcertOption concertOption) {
        return concertOptionRepository.update(concertOption);
    }

    @Override
    public List<ConcertOption> getConcertOptionData(Long concertId) {
        return concertOptionRepository.getConcertOptionData(concertId);
    }

    @Override
    public ConcertOption getConcertOptionDataByLocalDate(Long concertOptionId) {
        return concertOptionRepository.getConcertOptionDataByLocalDate(concertOptionId);
    }

    @Override
    public ConcertOption getConcertOptionDataById(Long concertOptionid) {
        return concertOptionRepository.getConcertOptionDataById(concertOptionid);
    }

    @Override
    public List<LocalDateTime> getConcertDate(Long concertId) {
        return concertOptionRepository.getConcertDate(concertId);
    }
}

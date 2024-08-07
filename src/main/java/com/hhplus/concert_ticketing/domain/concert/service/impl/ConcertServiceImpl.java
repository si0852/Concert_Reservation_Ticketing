package com.hhplus.concert_ticketing.domain.concert.service.impl;

import com.hhplus.concert_ticketing.domain.concert.entity.Concert;
import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import com.hhplus.concert_ticketing.domain.concert.repository.ConcertOptionRepository;
import com.hhplus.concert_ticketing.domain.concert.repository.ConcertRepository;
import com.hhplus.concert_ticketing.domain.concert.repository.SeatRepository;
import com.hhplus.concert_ticketing.domain.concert.service.ConcertService;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.util.exception.LockException;
import com.hhplus.concert_ticketing.util.exception.NoInfoException;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConcertServiceImpl implements ConcertService {

    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;
    private final ConcertOptionRepository concertOptionRepository;

    public ConcertServiceImpl(ConcertRepository concertRepository, SeatRepository seatRepository, ConcertOptionRepository concertOptionRepository) {
        this.concertRepository = concertRepository;
        this.seatRepository = seatRepository;
        this.concertOptionRepository = concertOptionRepository;
    }

    @CacheEvict(value = "concertData", key = "#concert.concertId")
    @Transactional
    @Override
    public Concert saveConcertData(Concert concert) {
        return concertRepository.saveData(concert);
    }

    @CacheEvict(value = "concertData", key = "#concert.concertId")
    @Transactional
    @Override
    public Concert updateConcertData(Concert concert) {
        return concertRepository.saveData(concert);
    }

    @Cacheable(value = "concertData", key = "#concertId")
    @Transactional
    @Override
    public Concert getConcertData(Long concertId) {
        Concert concertData = concertRepository.getConcertData(concertId);
        if (concertData == null) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "콘서트 정보가 없습니다.", null));
        return concertData;
    }

    @Cacheable("concertData")
    @Transactional
    @Override
    public List<Concert> getConcertData() {
        return concertRepository.getConcertData();
    }


    @Cacheable(value = "concertOptionData", key = "#concertId", condition = "#concertId!=null")
    @Transactional
    @Override
    public List<ConcertOption> getConcertOptionData(Long concertId) {
        List<ConcertOption> concertOptionData = concertOptionRepository.getConcertOptionData(concertId);
        if(concertOptionData.isEmpty()) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "콘서트 정보가 없습니다.", 0));
        return concertOptionData;
    }


    @Override
    public ConcertOption getConcertOptionDataByLocalDate(Long concertOptionId) {
        ConcertOption concertOptionDataByLocalDate = concertOptionRepository.getConcertOptionDataByLocalDate(concertOptionId);
        if(concertOptionDataByLocalDate == null) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "오픈된 콘서트 정보가 없습니다.", null));
        return concertOptionDataByLocalDate;
    }

    @Override
    public ConcertOption getConcertOptionDataById(Long concertOptionId) {
        ConcertOption concertOptionDataById = concertOptionRepository.getConcertOptionDataById(concertOptionId);
        if(concertOptionDataById == null) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "콘서트 상세 정보가 없습니다.", null));
        return concertOptionDataById;
    }

    @Override
    public List<LocalDateTime> getConcertDate(Long concertId) {
        List<LocalDateTime> concertDate = concertOptionRepository.getConcertDate(concertId);
        if(concertDate.size() == 0) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "콘서트 상세 정보가 없습니다.", 0));
        return concertDate;
    }

    @CacheEvict(value = "concertOptionData", key = "#concertOption.concertOptionId")
    @Override
    public ConcertOption saveConcertOption(ConcertOption concertOption) {
        return concertOptionRepository.saveData(concertOption);
    }

    @CacheEvict(value = "concertOptionData", key = "#concertOption.concertOptionId")
    @Override
    public ConcertOption updateConcertOption(ConcertOption concertOption) {
        return concertOptionRepository.update(concertOption);
    }


    @Transactional
    @Override
    public Seat saveSeatData(Seat seat) {
        return seatRepository.saveData(seat);
    }

    @Transactional
    @Override
    public List<Seat> getSeatData(Long concertOptionId, String status) {
        List<Seat> seatData = seatRepository.getSeatData(concertOptionId, status);
        if(seatData.size() == 0) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "예약가능한 좌석이 없습니다.", 0));
        return seatData;
    }

    @Transactional
    @Override
    public Seat getSeatOnlyData(Long seatId) {
        try {
            Seat seatData = seatRepository.getSeatData(seatId);
            // 좌석정보가 없다면
            if (seatData == null)throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "좌석 정보가 없습니다.", null));
            return seatData;
        } catch (OptimisticLockException e) {
            throw new LockException(new ResponseDto(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null));
        }
    }

    @Override
    public Seat updateSeatData(Seat seat) {
        return seatRepository.update(seat);
    }
}

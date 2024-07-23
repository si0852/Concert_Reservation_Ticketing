package com.hhplus.concert_ticketing.business.service.impl;

import com.hhplus.concert_ticketing.business.entity.Concert;
import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.entity.Seat;
import com.hhplus.concert_ticketing.business.repository.ConcertOptionRepository;
import com.hhplus.concert_ticketing.business.repository.ConcertRepository;
import com.hhplus.concert_ticketing.business.repository.SeatRepository;
import com.hhplus.concert_ticketing.business.service.ConcertService;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.util.exception.NoInfoException;
import jakarta.servlet.http.HttpServletResponse;
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

    @Override
    public Concert saveConcertData(Concert concert) {
        return concertRepository.saveData(concert);
    }

    @Override
    public Concert getConcertData(Long concertId) {
        Concert concertData = concertRepository.getConcertData(concertId);
        if (concertData == null) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "콘서트 정보가 없습니다.", null));
        return concertData;
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
        List<ConcertOption> concertOptionData = concertOptionRepository.getConcertOptionData(concertId);
        if(concertOptionData.size() == 0) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "콘서트 정보가 없습니다.", 0));
        return concertOptionData;
    }

    @Override
    public List<Concert> getConcertData() {
        return concertRepository.getConcertData();
    }

    @Override
    public ConcertOption getConcertOptionDataByLocalDate(Long concertOptionId) {
        ConcertOption concertOptionDataByLocalDate = concertOptionRepository.getConcertOptionDataByLocalDate(concertOptionId);
        if(concertOptionDataByLocalDate == null) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "오픈된 콘서트 정보가 없습니다.", null));
        return concertOptionDataByLocalDate;
    }

    @Override
    public ConcertOption getConcertOptionDataById(Long concertOptionid) {
        ConcertOption concertOptionDataById = concertOptionRepository.getConcertOptionDataById(concertOptionid);
        if(concertOptionDataById == null) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "콘서트 상세 정보가 없습니다.", null));
        return concertOptionDataById;
    }

    @Override
    public List<LocalDateTime> getConcertDate(Long concertId) {
        List<LocalDateTime> concertDate = concertOptionRepository.getConcertDate(concertId);
        if(concertDate.size() == 0) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "콘서트 상세 정보가 없습니다.", 0));
        return concertDate;
    }

    @Override
    public Seat saveSeatData(Seat seat) {
        return seatRepository.saveData(seat);
    }

    @Override
    public List<Seat> getSeatData(Long concertOptionId, String status) {
        List<Seat> seatData = seatRepository.getSeatData(concertOptionId, status);
        if(seatData.size() == 0) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "예약가능한 좌석이 없습니다.", 0));
        return seatData;
    }

    @Transactional
    @Override
    public Seat getSeatOnlyData(Long seatId) {
        Seat seatData = seatRepository.getSeatData(seatId);
        // 좌석정보가 없다면
        if(seatData == null) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "좌석 정보가 없습니다.", null));
        return seatData;
    }

    @Override
    public Seat updateSeatData(Seat seat) {
        return seatRepository.update(seat);
    }
}

package com.hhplus.concert_ticketing.domain.concert.mapper;

import com.hhplus.concert_ticketing.domain.concert.entity.Concert;
import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import com.hhplus.concert_ticketing.presentation.concert.dto.ConcertDto;
import com.hhplus.concert_ticketing.presentation.concert.dto.ConcertOptionDto;
import com.hhplus.concert_ticketing.presentation.concert.dto.SeatDto;

public class ConcertMapper {

    public static ConcertDto toDto(Concert concert) {
        ConcertDto dto = new ConcertDto();
        dto.setId(concert.getConcertId());
        dto.setName(concert.getName());
        return dto;
    }

    public static ConcertOptionDto toDto(ConcertOption concertOption) {
        ConcertOptionDto dto = new ConcertOptionDto();
        dto.setConcertOptionId(concertOption.getConcertOptionId());
        dto.setConcertId(concertOption.getConcertId());
        dto.setConcertDate(concertOption.getConcertDate());
        dto.setPrice(concertOption.getPrice());
        return dto;
    }

    public static SeatDto toDto(Seat seat) {
        SeatDto dto = new SeatDto();
        dto.setSeatId(seat.getSeatId());
        dto.setConcertOptionId(seat.getConcertOptionId());
        dto.setSeatNumber(seat.getSeatNumber());
        return dto;
    }
}

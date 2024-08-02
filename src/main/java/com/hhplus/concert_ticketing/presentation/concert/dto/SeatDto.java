package com.hhplus.concert_ticketing.presentation.concert.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {
    private Long seatId;
    private Long concertOptionId;
    private String seatNumber;
}

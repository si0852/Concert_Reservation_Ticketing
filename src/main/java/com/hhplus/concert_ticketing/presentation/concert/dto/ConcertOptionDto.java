package com.hhplus.concert_ticketing.presentation.concert.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConcertOptionDto {
    private Long concertOptionId;;
    private Long concertId;
    private LocalDateTime concertDate;
    private Double price;
}

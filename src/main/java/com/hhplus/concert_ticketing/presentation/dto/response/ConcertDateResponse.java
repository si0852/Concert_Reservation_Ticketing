package com.hhplus.concert_ticketing.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertDateResponse {
    Long concertOptionId;
    String concertDate;

    @Builder
    public ConcertDateResponse(Long concertOptionId, String concertDate) {
        this.concertOptionId = concertOptionId;
        this.concertDate = concertDate;
    }
}

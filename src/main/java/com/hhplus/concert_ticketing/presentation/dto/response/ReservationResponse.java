package com.hhplus.concert_ticketing.presentation.dto.response;


import lombok.Builder;
import lombok.Getter;

@Getter
public class ReservationResponse {

    Long reservationId;

    @Builder
    public ReservationResponse(Long reservationId) {
        this.reservationId = reservationId;
    }
}//end

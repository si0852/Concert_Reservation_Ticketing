package com.hhplus.concert_ticketing.presentation.dto.response;

import com.hhplus.concert_ticketing.status.SeatStatus;
import lombok.Getter;

@Getter
public class SeatResponse {
    Long seatId;
    String seatNumber;
    String status;

    public SeatResponse(Long seatId, String seatNumber, String status) {
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.status = status;
    }
}

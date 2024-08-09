package com.hhplus.concert_ticketing.infra.api;

import com.hhplus.concert_ticketing.domain.reservation.entity.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataPlatformFakeApi {
    public void sendReservationInfo(Reservation reservation) {
        try {
            Thread.sleep(1000);
            log.info("예약정보 전송 : {}", reservation);
        } catch (InterruptedException e) {
            log.error("예약정보 실패 : {}", e.getMessage(), e);
        }
    }
}

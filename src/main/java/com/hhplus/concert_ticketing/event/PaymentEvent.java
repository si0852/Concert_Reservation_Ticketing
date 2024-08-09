package com.hhplus.concert_ticketing.event;

import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.reservation.entity.Reservation;
import org.springframework.context.ApplicationEvent;

public class PaymentEvent extends ApplicationEvent {

    private final Reservation reservation;
    private final String userId;

    public PaymentEvent(Object source, Reservation reservation,String userId) {
        super(source);
        this.reservation = reservation;
        this.userId = userId;
    }

    public Reservation getReservation() {
        return reservation;
    }


    public String getUserId() {
        return userId;
    }
}

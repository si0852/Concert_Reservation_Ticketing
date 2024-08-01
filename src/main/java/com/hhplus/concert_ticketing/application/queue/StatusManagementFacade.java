package com.hhplus.concert_ticketing.application.queue;

public interface StatusManagementFacade {

    void expiredToken();

    void convertToActive();

    void expiredReservationStatus();
}

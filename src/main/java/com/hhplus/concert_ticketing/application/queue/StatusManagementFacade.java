package com.hhplus.concert_ticketing.application.queue;

public interface StatusManagementFacade {

    Boolean expiredToken();

    Boolean expiredReservationStatus();
}

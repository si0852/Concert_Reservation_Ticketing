package com.hhplus.concert_ticketing.application.facade;

public interface StatusManagementFacade {

    Boolean expiredToken();

    Boolean expiredReservationStatus();
}

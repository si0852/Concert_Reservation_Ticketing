package com.hhplus.concert_ticketing.presentation.schedule;

import com.hhplus.concert_ticketing.application.facade.StatusManagementFacade;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerService {

    private final StatusManagementFacade statusManagementFacade;

    public SchedulerService(StatusManagementFacade statusManagementFacade) {
        this.statusManagementFacade = statusManagementFacade;
    }

    @Scheduled(fixedDelay = 60*1000)
    public void expiredToken() {
        statusManagementFacade.expiredToken();
    }


    @Scheduled(fixedDelay = 60*3*1000)
    public void expiredReservation() {
        statusManagementFacade.expiredReservationStatus();
    }
}

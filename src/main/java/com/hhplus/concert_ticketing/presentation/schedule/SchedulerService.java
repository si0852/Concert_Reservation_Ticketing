package com.hhplus.concert_ticketing.presentation.schedule;

import com.hhplus.concert_ticketing.application.facade.ScheduleManagementFacade;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerService {

    private final ScheduleManagementFacade scheduleManagementFacade;

    public SchedulerService(ScheduleManagementFacade scheduleManagementFacade) {
        this.scheduleManagementFacade = scheduleManagementFacade;
    }

    @Scheduled(fixedDelay = 60000)
    public void expiredToken() {
        scheduleManagementFacade.expiredToken();
    }
}

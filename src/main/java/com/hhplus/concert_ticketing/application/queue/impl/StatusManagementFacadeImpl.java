package com.hhplus.concert_ticketing.application.queue.impl;

import com.hhplus.concert_ticketing.application.queue.StatusManagementFacade;
import com.hhplus.concert_ticketing.domain.reservation.entity.Reservation;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.domain.concert.service.ConcertService;
import com.hhplus.concert_ticketing.domain.reservation.service.ReservationService;
import com.hhplus.concert_ticketing.domain.queue.service.TokenService;
import com.hhplus.concert_ticketing.domain.user.mapper.UserMapper;
import com.hhplus.concert_ticketing.domain.user.service.impl.UserQueueService;
import com.hhplus.concert_ticketing.presentation.queue.dto.ScoreDto;
import com.hhplus.concert_ticketing.status.ReservationStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import lombok.extern.java.Log;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StatusManagementFacadeImpl implements StatusManagementFacade {


    private final UserQueueService userQueueService;
    private final static String activeKey = "Active";
    private final static String waitKey = "Waiting";
    private final static long maxActive = 40;


    public StatusManagementFacadeImpl(UserQueueService userQueueService) {
        this.userQueueService = userQueueService;
    }

    @Override
    public void expiredToken() {
        Set<ZSetOperations.TypedTuple<Object>> activeSet = userQueueService.countActiveUser().rangeWithScores(activeKey, 0, -1);
        List<ScoreDto> activeList = activeSet.stream().map(UserMapper::toDto).collect(Collectors.toList());
        for (ScoreDto active: activeList) {
            double compareScore = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            double calScore = compareScore - active.getScore();

            if (calScore >= 300) {
                userQueueService.deleteToActiveQueue(active.getUserId());
            }
        }
    }

    @Override
    public void convertToActive() {
        Long size = userQueueService.countActiveUser().size(activeKey);
        Long count = maxActive - size;

        if (count > 0) {
            SetOperations<String, Object> user = userQueueService.countWaitingUser();
            for (int i = 0; i < count; i++) {
                String content = String.valueOf(user.pop(waitKey));
                user.remove(waitKey, content);
                userQueueService.convertWaitingToActive(content);
            }
        }
    }


    @Override
    public void expiredReservationStatus() {
//        return true;
    }

}

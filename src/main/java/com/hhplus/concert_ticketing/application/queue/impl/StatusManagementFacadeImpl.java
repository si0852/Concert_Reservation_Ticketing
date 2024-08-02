package com.hhplus.concert_ticketing.application.queue.impl;

import com.hhplus.concert_ticketing.application.queue.StatusManagementFacade;
import com.hhplus.concert_ticketing.domain.user.mapper.UserMapper;
import com.hhplus.concert_ticketing.domain.user.service.impl.UserQueueService;
import com.hhplus.concert_ticketing.presentation.queue.dto.ScoreDto;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StatusManagementFacadeImpl implements StatusManagementFacade {

//    private final TokenService tokenService;
//    private final ReservationService reservationService;
//    private final ConcertService concertService;
//
//    public StatusManagementFacadeImpl(TokenService tokenService, ReservationService reservationService, ConcertService concertService) {
//        this.tokenService = tokenService;
//        this.reservationService = reservationService;
//        this.concertService = concertService;
//    }

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

//    private void backup() {
//        try {
//            List<Token> tokenListByActive = tokenService.getTokenListByStatus(TokenStatus.ACTIVE.toString());
//
//            for (Token token: tokenListByActive) {
//                LocalDateTime createdAt = token.getCreatedAt();
//                LocalDateTime expiredAt = token.getExpiresAt();
//                Long seconds = Duration.between(createdAt, expiredAt).getSeconds();
//                if (seconds > 300L){
//                    token.changeExpired();
//                    tokenService.updateToken(token);
//                }
//            }
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }


    @Override
    public void expiredReservationStatus() {
//        return true;
    }

//    private void backup2() {
//        try {
//            String status = ReservationStatus.WAITING.toString();
//            List<Reservation> reservationDataByStatus = reservationService.getReservationDataByStatus(status);
//
//            for (Reservation reservation : reservationDataByStatus) {
//                LocalDateTime createdAt = reservation.getCreatedAt();
//                LocalDateTime expiredAt = reservation.getUpdatedAt();
//                Long seconds = Duration.between(createdAt, expiredAt).getSeconds();
//                if (seconds > 300L) {
//                    reservation.changeStateCancel();
//                    reservationService.UpdateReservationData(reservation);
//                    Seat seatData = concertService.getSeatOnlyData(reservation.getSeatId());
//                    seatData.changeStateUnlock();
//                    concertService.updateSeatData(seatData);
//                }
//
//            }
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
}

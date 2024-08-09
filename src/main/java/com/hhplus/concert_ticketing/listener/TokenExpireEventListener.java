package com.hhplus.concert_ticketing.listener;

import com.hhplus.concert_ticketing.domain.user.service.impl.UserQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class TokenExpireEventListener {

    private final UserQueueService userQueueService;

    public TokenExpireEventListener(UserQueueService userQueueService) {
        this.userQueueService = userQueueService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTokenExpireEvent(String userId) {
        try {
            userQueueService.expiredActiveQueue(userId);
        } catch (Exception e) {
            log.error("토큰 만료 처리에 실패 : {}", e.getMessage(), e );
        }
    }
}

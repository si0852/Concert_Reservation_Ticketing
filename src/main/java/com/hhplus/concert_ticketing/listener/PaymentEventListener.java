package com.hhplus.concert_ticketing.listener;

import com.hhplus.concert_ticketing.domain.user.service.impl.UserQueueService;
import com.hhplus.concert_ticketing.event.PaymentEvent;
import com.hhplus.concert_ticketing.infra.api.DataPlatformFakeApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class PaymentEventListener {

    private final DataPlatformFakeApi dataPlatformFakeApi;
    private final UserQueueService userQueueService;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentEventListener(DataPlatformFakeApi dataPlatformFakeApi, UserQueueService userQueueService, ApplicationEventPublisher eventPublisher) {
        this.dataPlatformFakeApi = dataPlatformFakeApi;
        this.userQueueService = userQueueService;
        this.eventPublisher = eventPublisher;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaidEvent(PaymentEvent paymentEvent) {
        try {
            dataPlatformFakeApi.sendReservationInfo(paymentEvent.getReservation());

            boolean checked = userQueueService.checkActiveUser(paymentEvent.getUserId());
            if (!checked) throw new RuntimeException("토큰이 존재 하지 않습니다.");

            eventPublisher.publishEvent(paymentEvent.getUserId());
        } catch (Exception e) {
            log.error("예약 정보 전달 실패 : {}", paymentEvent.getReservation(), e);
        }
    }
}

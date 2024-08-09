package com.hhplus.concert_ticketing.listener;

import com.hhplus.concert_ticketing.domain.reservation.entity.Reservation;
import com.hhplus.concert_ticketing.domain.user.service.impl.UserQueueService;
import com.hhplus.concert_ticketing.event.PaymentEvent;
import com.hhplus.concert_ticketing.infra.api.DataPlatformFakeApi;
import com.hhplus.concert_ticketing.status.ReservationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentEventListenerTest {

    @Mock
    private DataPlatformFakeApi dataPlatformFakeApi;

    @Mock
    private UserQueueService userQueueService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentEventListener paymentEventListener;

    @Test
    void testHandlePaidEvent_Successful() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        PaymentEvent paymentEvent = new PaymentEvent(this, new Reservation(1L, 1L, ReservationStatus.PAID.toString(), now, now.plusMinutes(4)), "userId");

        // Mocking
        when(userQueueService.checkActiveUser(paymentEvent.getUserId())).thenReturn(true);

        // When
        paymentEventListener.handlePaidEvent(paymentEvent);

        // Then
        verify(dataPlatformFakeApi).sendReservationInfo(paymentEvent.getReservation());
        verify(userQueueService).checkActiveUser(paymentEvent.getUserId());
        verify(eventPublisher).publishEvent(paymentEvent.getUserId());
    }

    @Test
    void testHandlePaidEvent_UserCheckFails() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        PaymentEvent paymentEvent = new PaymentEvent(this, new Reservation(1L, 1L, ReservationStatus.PAID.toString(), now, now.plusMinutes(4)), "userId");

        // Mocking
        when(userQueueService.checkActiveUser(paymentEvent.getUserId())).thenReturn(false);

        // When
        paymentEventListener.handlePaidEvent(paymentEvent);

        // Then
        verify(dataPlatformFakeApi).sendReservationInfo(any(Reservation.class));
        verify(userQueueService).checkActiveUser(paymentEvent.getUserId());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void testHandlePaidEvent_DataPlatformApiFails() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        PaymentEvent paymentEvent = new PaymentEvent(this, new Reservation(1L, 1L, ReservationStatus.PAID.toString(), now, now.plusMinutes(4)), "userId");

        // Mocking
        doThrow(new RuntimeException("API error")).when(dataPlatformFakeApi).sendReservationInfo(any(Reservation.class));

        // When
        paymentEventListener.handlePaidEvent(paymentEvent);

        // Then
        verify(dataPlatformFakeApi).sendReservationInfo(any(Reservation.class));
        verify(userQueueService, never()).checkActiveUser(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
package com.hhplus.concert_ticketing.application.facade;

import com.hhplus.concert_ticketing.domain.payment.entity.Payment;

public interface PaymentManagementFacade {

    Payment paymentProgress(Long reservationId, String token) throws Exception;
}

package com.hhplus.concert_ticketing.business.facade;

import com.hhplus.concert_ticketing.business.entity.Payment;

public interface PaymentManagementFacade {

    Payment paymentProgress(Long reservationId, String token);
}

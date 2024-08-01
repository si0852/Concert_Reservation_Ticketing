package com.hhplus.concert_ticketing.domain.payment.repository;

import com.hhplus.concert_ticketing.domain.payment.entity.Payment;

import java.util.List;

public interface PaymentRepository {

    Payment saveData(Payment payment);

    Payment updateData(Payment payment);

    List<Payment> getPaymentData(Long reservationId);

}

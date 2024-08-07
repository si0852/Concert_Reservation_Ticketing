package com.hhplus.concert_ticketing.domain.payment.service;

import com.hhplus.concert_ticketing.domain.payment.entity.Payment;

import java.util.List;

public interface PaymentService {

    public Payment savePayment(Payment payment);

    public Payment updatePayment(Payment payment);

    public List<Payment> getPaymentData(Long reservationId);

}

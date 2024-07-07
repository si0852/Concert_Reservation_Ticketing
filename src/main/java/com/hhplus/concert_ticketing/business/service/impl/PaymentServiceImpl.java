package com.hhplus.concert_ticketing.business.service.impl;

import com.hhplus.concert_ticketing.business.entity.Payment;
import com.hhplus.concert_ticketing.business.repository.PaymentRepository;
import com.hhplus.concert_ticketing.business.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment savePayment(Payment payment) {
        return paymentRepository.saveData(payment);
    }

    @Override
    public Payment updatePayment(Payment payment) {
        return paymentRepository.updateData(payment);
    }

    @Override
    public List<Payment> getPaymentData(Long reservationId) {
        return paymentRepository.getPaymentData(reservationId);
    }

}

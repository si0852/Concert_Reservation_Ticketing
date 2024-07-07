package com.hhplus.concert_ticketing.business.repository.impl;

import com.hhplus.concert_ticketing.business.entity.Payment;
import com.hhplus.concert_ticketing.business.repository.PaymentRepository;
import com.hhplus.concert_ticketing.infra.JpaPaymentRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;

    public PaymentRepositoryImpl(JpaPaymentRepository jpaPaymentRepository) {
        this.jpaPaymentRepository = jpaPaymentRepository;
    }

    @Override
    public Payment saveData(Payment payment) {
        return jpaPaymentRepository.save(payment);
    }

    @Override
    public Payment updateData(Payment payment) {
        return jpaPaymentRepository.save(payment);
    }

    @Override
    public List<Payment> getPaymentData(Long reservationId) {
        return jpaPaymentRepository.findByReservationId(reservationId);
    }

}

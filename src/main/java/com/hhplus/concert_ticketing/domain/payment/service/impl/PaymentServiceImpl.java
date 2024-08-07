package com.hhplus.concert_ticketing.domain.payment.service.impl;

import com.hhplus.concert_ticketing.domain.payment.entity.Payment;
import com.hhplus.concert_ticketing.domain.payment.repository.PaymentRepository;
import com.hhplus.concert_ticketing.domain.payment.service.PaymentService;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.util.exception.NoInfoException;
import jakarta.servlet.http.HttpServletResponse;
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
        List<Payment> paymentData = paymentRepository.getPaymentData(reservationId);
        if(paymentData.size() == 0) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "결제정보가 없습니다.", null));
        return paymentData;
    }

}

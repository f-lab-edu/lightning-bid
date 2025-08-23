package com.lightningbid.payments.service;

import com.lightningbid.payments.domain.model.Payment;
import com.lightningbid.payments.domain.model.TossPayment;
import com.lightningbid.payments.domain.repository.PaymentRepository;
import com.lightningbid.payments.exception.PaymentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PaymentFailureService {

    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(Long payment_id, String errorCode, String errorMessage) {

        Payment payment = paymentRepository.findById(payment_id)
                .orElseThrow(PaymentNotFoundException::new);

        payment.paymentFail();

        payment.getTossPayments().add(TossPayment.builder()
                .orderId(payment.getOrderId())
                .failureCode(errorCode)
                .failureMessage(errorMessage)
                .payment(payment)
                .build());
    }
}

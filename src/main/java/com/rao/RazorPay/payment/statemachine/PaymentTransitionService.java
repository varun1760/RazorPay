package com.rao.RazorPay.payment.statemachine;

import com.rao.RazorPay.common.enums.PaymentActor;
import com.rao.RazorPay.common.enums.PaymentEvent;
import com.rao.RazorPay.common.enums.PaymentStatus;
import com.rao.RazorPay.payment.entity.Payment;
import com.rao.RazorPay.payment.entity.PaymentTransitionLog;
import com.rao.RazorPay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event) {
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), event);
        payment.setStatus(next);
        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(event)
                .toStatus(next)
                .actor(PaymentActor.SYSTEM)
                .occurredAt(LocalDateTime.now())
                .build();
        paymentTransitionLogRepository.save(log);
        return next;
    }
}

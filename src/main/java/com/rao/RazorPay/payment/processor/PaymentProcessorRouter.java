package com.rao.RazorPay.payment.processor;

import com.rao.RazorPay.common.enums.PaymentMethod;
import com.rao.RazorPay.payment.dto.request.PaymentProcessorRequest;
import com.rao.RazorPay.payment.dto.response.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentProcessorRouter {

    private Map<PaymentMethod, PaymentProcessor> paymentProcessorMap;

    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        PaymentProcessor paymentProcessor = paymentProcessorMap.get(request.method());
        if (paymentProcessor == null) {
            throw new IllegalArgumentException(String.format("Payment processor method %s is not registered", request.method()));
        }
        return paymentProcessor.charge(request);
    }
}

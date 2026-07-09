package com.rao.RazorPay.payment.processor;

import com.rao.RazorPay.payment.dto.request.PaymentProcessorRequest;
import com.rao.RazorPay.payment.dto.response.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);

}

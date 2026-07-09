package com.rao.RazorPay.payment.dto.request;

import com.rao.RazorPay.common.entity.Money;
import com.rao.RazorPay.common.enums.PaymentMethod;

import java.util.Map;

public record PaymentProcessorRequest(
        PaymentMethod method,
        Money amount,
        Map<String, Object> methodDetails
) {
}

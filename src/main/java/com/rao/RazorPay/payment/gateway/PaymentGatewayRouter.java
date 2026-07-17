package com.rao.RazorPay.payment.gateway;

import com.rao.RazorPay.common.enums.PaymentMethod;
import com.rao.RazorPay.payment.dto.request.PaymentRequest;
import com.rao.RazorPay.payment.dto.response.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentGatewayRouter {

    private final Map<PaymentMethod, PaymentAdapter> paymentAdapterMap;

    public PaymentResult initiate(PaymentRequest paymentRequest) {
        PaymentAdapter adapter = paymentAdapterMap.get(paymentRequest.method());
        if (adapter == null) {
            throw new IllegalArgumentException("No Payment adapter registered for method " + paymentRequest.method());
        }
        return adapter.initiate(paymentRequest);
    }

    public PaymentResult capture(PaymentMethod method, UUID paymentId) {
        PaymentAdapter adapter = paymentAdapterMap.get(method);
        if (adapter == null) {
            throw new IllegalArgumentException("No Payment adapter registered for method " + method);
        }
        return adapter.capture(paymentId);
    }
}

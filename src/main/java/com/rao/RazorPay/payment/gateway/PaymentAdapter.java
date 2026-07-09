package com.rao.RazorPay.payment.gateway;

import com.rao.RazorPay.payment.dto.request.PaymentRequest;
import com.rao.RazorPay.payment.dto.response.PaymentResult;

public interface PaymentAdapter {

    PaymentResult initiate(PaymentRequest paymentRequest);

}

package com.rao.RazorPay.payment.gateway.adapter;

import com.rao.RazorPay.payment.gateway.PaymentAdapter;
import com.rao.RazorPay.payment.dto.request.PaymentRequest;
import com.rao.RazorPay.payment.dto.response.PaymentResult;

public class NetBankingPaymentAdapter implements PaymentAdapter {
    @Override
    public PaymentResult initiate(PaymentRequest paymentRequest) {
        return null;
    }
}

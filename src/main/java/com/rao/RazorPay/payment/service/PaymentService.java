package com.rao.RazorPay.payment.service;

import com.rao.RazorPay.payment.dto.request.PaymentInitRequest;
import com.rao.RazorPay.payment.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse initiate(UUID merchantId, PaymentInitRequest request);
}

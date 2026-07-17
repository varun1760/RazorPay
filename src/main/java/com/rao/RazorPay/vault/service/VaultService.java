package com.rao.RazorPay.vault.service;

import com.rao.RazorPay.common.entity.Money;
import com.rao.RazorPay.payment.dto.response.PaymentProcessorResponse;
import com.rao.RazorPay.vault.dto.request.TokenizeRequest;
import com.rao.RazorPay.vault.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);
}

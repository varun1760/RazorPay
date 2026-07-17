package com.rao.RazorPay.payment.gateway.adapter;

import com.rao.RazorPay.payment.dto.response.PaymentProcessorResponse;
import com.rao.RazorPay.payment.gateway.PaymentAdapter;
import com.rao.RazorPay.payment.dto.request.PaymentRequest;
import com.rao.RazorPay.payment.dto.response.PaymentResult;
import com.rao.RazorPay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {

    private final VaultService vaultService;

    @Override
    public PaymentResult initiate(PaymentRequest paymentRequest) {

        String token = paymentRequest.methodDetails().get("token").toString();

        PaymentProcessorResponse response = vaultService.charge(
                paymentRequest.paymentId(), token, paymentRequest.amount(), paymentRequest.methodDetails()
        );

        return switch (response) {
            case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());
            case PaymentProcessorResponse.Failure failure -> new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessorResponse.Pending pending -> new PaymentResult.Pending(pending.processorReference());
        };
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("CARD_REFERENCE");
    }
}

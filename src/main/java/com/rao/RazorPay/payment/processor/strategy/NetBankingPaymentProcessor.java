package com.rao.RazorPay.payment.processor.strategy;

import com.rao.RazorPay.common.util.RandomizerUtil;
import com.rao.RazorPay.payment.processor.PaymentProcessor;
import com.rao.RazorPay.payment.dto.request.PaymentProcessorRequest;
import com.rao.RazorPay.payment.dto.response.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

@Component
public class NetBankingPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        final String BANK_CODE_FAIL = "BANK_CODE_FAIL";

        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("bank").toString() : null;

        if (BANK_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessorResponse.Failure("BANK_REJECTED",
                    "Bank rejected the transaction registration");
        }

        String processorReference = "NET_BANKING_PROCESSOR_" + RandomizerUtil.randomBase64(16);
//        String redirectReference = "http://REDIRECT_BANK.com/" + processorReference;

        return new PaymentProcessorResponse.Pending(processorReference);
    }
}

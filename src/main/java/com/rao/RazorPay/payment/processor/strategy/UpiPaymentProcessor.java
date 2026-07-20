package com.rao.RazorPay.payment.processor.strategy;

import com.rao.RazorPay.common.util.RandomizerUtil;
import com.rao.RazorPay.payment.processor.PaymentProcessor;
import com.rao.RazorPay.payment.dto.request.PaymentProcessorRequest;
import com.rao.RazorPay.payment.dto.response.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

@Component
public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        final String VPA_CODE_FAIL = "fail@okaxis";

        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("vpa").toString() : null;

        if (VPA_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessorResponse.Failure("UPI_REJECTED",
                    "Bank rejected the transaction registration");
        }

        String processorReference = "UPI_PROCESSOR_" + RandomizerUtil.randomBase64(16);
//        String bankReference = "BANK_REFERENCE_" + RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.Pending(processorReference);
    }
}

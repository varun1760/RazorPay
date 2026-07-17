package com.rao.RazorPay.payment.processor.strategy;

import com.rao.RazorPay.common.util.RandomizerUtil;
import com.rao.RazorPay.payment.processor.PaymentProcessor;
import com.rao.RazorPay.payment.dto.request.PaymentProcessorRequest;
import com.rao.RazorPay.payment.dto.response.PaymentProcessorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardPaymentProcessor implements PaymentProcessor {

    public static final String PAN_CARD_DECLINED = "400000000000002";
    public static final String PAN_CARD_EXPIRED = "400000000000009";

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        String pan = request.pan();
        if (pan.equals(PAN_CARD_DECLINED)) {
            log.warn("CARD DECLINED");
            return new PaymentProcessorResponse.Failure("CARD_DECLINED", "Card declined by Bank");
        }
        if (pan.equals(PAN_CARD_EXPIRED)) {
            log.warn("PAN EXPIRED");
            return new PaymentProcessorResponse.Failure("CARD_EXPIRED", "Card has expired");
        }

        String processorReference = "NET_BANKING_PROCESSOR_" + RandomizerUtil.randomBase64(16);
        return new PaymentProcessorResponse.Pending(processorReference);
    }
}

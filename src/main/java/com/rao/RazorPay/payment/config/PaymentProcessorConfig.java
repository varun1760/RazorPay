package com.rao.RazorPay.payment.config;

import com.rao.RazorPay.common.enums.PaymentMethod;
import com.rao.RazorPay.payment.processor.PaymentProcessor;
import com.rao.RazorPay.payment.processor.strategy.CardPaymentProcessor;
import com.rao.RazorPay.payment.processor.strategy.NetBankingPaymentProcessor;
import com.rao.RazorPay.payment.processor.strategy.UpiPaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentProcessorConfig {

    private final CardPaymentProcessor cardPaymentProcessor;
    private final NetBankingPaymentProcessor netBankingPaymentProcessor;
    private final UpiPaymentProcessor upiPaymentProcessor;

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap() {
        return Map.of(
                PaymentMethod.CARD, cardPaymentProcessor,
                PaymentMethod.NETBANKING, netBankingPaymentProcessor,
                PaymentMethod.UPI, upiPaymentProcessor
        );
    }
}

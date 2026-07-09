package com.rao.RazorPay.payment.config;

import com.rao.RazorPay.common.enums.PaymentMethod;
import com.rao.RazorPay.payment.processor.PaymentProcessor;
import com.rao.RazorPay.payment.processor.strategy.CardPaymentProcessor;
import com.rao.RazorPay.payment.processor.strategy.NetBankingPaymentProcessor;
import com.rao.RazorPay.payment.processor.strategy.UpiPaymentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentProcessorConfig {

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap() {
        return Map.of(
                PaymentMethod.CARD, new CardPaymentProcessor(),
                PaymentMethod.NETBANKING, new NetBankingPaymentProcessor(),
                PaymentMethod.UPI, new UpiPaymentProcessor()
        );
    }
}

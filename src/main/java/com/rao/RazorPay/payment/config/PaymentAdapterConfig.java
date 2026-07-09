package com.rao.RazorPay.payment.config;

import com.rao.RazorPay.common.enums.PaymentMethod;
import com.rao.RazorPay.payment.gateway.PaymentAdapter;
import com.rao.RazorPay.payment.gateway.adapter.CardPaymentAdapter;
import com.rao.RazorPay.payment.gateway.adapter.NetBankingPaymentAdapter;
import com.rao.RazorPay.payment.gateway.adapter.UpiPaymentAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentAdapterConfig {

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
                PaymentMethod.CARD, new CardPaymentAdapter(),
                PaymentMethod.NETBANKING, new NetBankingPaymentAdapter(),
                PaymentMethod.UPI, new UpiPaymentAdapter()
        );
    }
}

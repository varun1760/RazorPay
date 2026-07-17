package com.rao.RazorPay.payment.config;

import com.rao.RazorPay.common.enums.PaymentMethod;
import com.rao.RazorPay.payment.gateway.PaymentAdapter;
import com.rao.RazorPay.payment.gateway.adapter.CardPaymentAdapter;
import com.rao.RazorPay.payment.gateway.adapter.NetBankingPaymentAdapter;
import com.rao.RazorPay.payment.gateway.adapter.UpiPaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {

    private final CardPaymentAdapter cardPaymentAdapter;
    private final NetBankingPaymentAdapter netBankingPaymentAdapter;
    private final UpiPaymentAdapter upiPaymentAdapter;

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
                PaymentMethod.CARD, cardPaymentAdapter,
                PaymentMethod.NETBANKING, netBankingPaymentAdapter,
                PaymentMethod.UPI, upiPaymentAdapter
        );
    }
}

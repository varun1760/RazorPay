package com.rao.RazorPay.payment.dto.response;

public sealed interface PaymentResult permits
        PaymentResult.Pending,
        PaymentResult.Failure {

    record Pending(String registrationReference) implements PaymentResult {}

    record Failure(String errorCode, String errorDescription) implements PaymentResult {}

}

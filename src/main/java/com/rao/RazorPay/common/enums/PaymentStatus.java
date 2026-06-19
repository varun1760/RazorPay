package com.rao.RazorPay.common.enums;

public enum PaymentStatus {
    CREATED,
    AUTHORIZING,
    AUTHORIZED,
    CAPTURING,
    CAPTURED,
    CANCELED,
    REFUNDED,
    FAILED,
    PARTIALLY_REFUNDED,
    SETTLED,
    AUTH_EXPIRED
}

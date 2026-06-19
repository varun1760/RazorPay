package com.rao.RazorPay.common.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class Money {
    private int amountUnits;
    private String currency;

    private Money(int amountUnits, String currency) {
        this.amountUnits = amountUnits;
        this.currency = currency;
    }

    public Money add(Money money) {
        if (!this.currency.equals(money.currency)) {
            throw new IllegalArgumentException("currency is not equal");
        }
        return new Money(this.amountUnits + money.amountUnits, this.currency);
    }
    public Money subtract(Money money) {
        if (!this.currency.equals(money.currency)) {
            throw new IllegalArgumentException("currency is not equal");
        }
        return new Money(this.amountUnits - money.amountUnits, this.currency);
    }

    public static Money of(int amountUnits, String currency) {
        return new Money(amountUnits, currency);
    }

    public static Money inr(int amountUnits) {
        return new Money(amountUnits, "INR");
    }
}

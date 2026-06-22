package com.rao.RazorPay.merchant.dto.response;

import com.rao.RazorPay.common.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponse (
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {
}

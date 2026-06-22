package com.rao.RazorPay.merchant.dto.request;

import com.rao.RazorPay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}

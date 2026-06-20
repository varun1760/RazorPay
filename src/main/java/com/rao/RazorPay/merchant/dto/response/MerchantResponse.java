package com.rao.RazorPay.merchant.dto.response;

import com.rao.RazorPay.common.enums.BusinessType;
import com.rao.RazorPay.common.enums.MerchantStatus;

import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus
) {
}

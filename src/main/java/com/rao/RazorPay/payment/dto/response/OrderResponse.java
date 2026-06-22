package com.rao.RazorPay.payment.dto.response;

import com.rao.RazorPay.common.entity.Money;
import com.rao.RazorPay.common.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID merchantId,
        String receipt,
        Money ampount,
        OrderStatus status,
        Integer attempts,
        Map<String, Object> notes,
        LocalDateTime expiredAt,
        LocalDateTime createdAt
) {
}

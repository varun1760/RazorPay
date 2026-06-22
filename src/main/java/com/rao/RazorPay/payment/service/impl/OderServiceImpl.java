package com.rao.RazorPay.payment.service.impl;

import com.rao.RazorPay.common.enums.OrderStatus;
import com.rao.RazorPay.common.exception.DuplicateResourceException;
import com.rao.RazorPay.payment.dto.request.CreateOrderRequest;
import com.rao.RazorPay.payment.dto.response.OrderResponse;
import com.rao.RazorPay.payment.entity.OrderRecord;
import com.rao.RazorPay.payment.repository.OrderRepository;
import com.rao.RazorPay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes;

    @Override
    public OrderResponse create(UUID merchantId, CreateOrderRequest request) {
        if (request.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId, request.receipt())) {
            log.error("Order with receipt {} already exists", request.receipt());
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE",
                    "Order with receipt " + request.receipt() + " already exists");
        }
        OrderRecord  orderRecord = OrderRecord.builder()
                .merchantId(merchantId)
                .receipt(request.receipt())
                .amount(request.amount())
                .notes(request.notes())
                .orderStatus(OrderStatus.CREATED)
                .expiredAt(request.expiredAt() != null ? request.expiredAt() : LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes))
                .build();
        orderRepository.save(orderRecord);
        return new OrderResponse(orderRecord.getId(),
                orderRecord.getMerchantId(),
                orderRecord.getReceipt(),
                orderRecord.getAmount(),
                orderRecord.getOrderStatus(),
                orderRecord.getAttempts(),
                orderRecord.getNotes(),
                orderRecord.getExpiredAt(),
                null);
    }
}

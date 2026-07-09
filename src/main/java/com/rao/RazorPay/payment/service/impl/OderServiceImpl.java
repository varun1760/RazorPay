package com.rao.RazorPay.payment.service.impl;

import com.rao.RazorPay.common.enums.OrderStatus;
import com.rao.RazorPay.common.exception.BusinessRuleViolationException;
import com.rao.RazorPay.common.exception.DuplicateResourceException;
import com.rao.RazorPay.common.exception.ResourceNotFoundException;
import com.rao.RazorPay.payment.dto.request.CreateOrderRequest;
import com.rao.RazorPay.payment.dto.response.OrderResponse;
import com.rao.RazorPay.payment.dto.response.PaymentResponse;
import com.rao.RazorPay.payment.entity.OrderRecord;
import com.rao.RazorPay.payment.entity.Payment;
import com.rao.RazorPay.payment.mapper.OrderMapper;
import com.rao.RazorPay.payment.mapper.PaymentMapper;
import com.rao.RazorPay.payment.repository.OrderRepository;
import com.rao.RazorPay.payment.repository.PaymentRepository;
import com.rao.RazorPay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes;

    @Override
    @Transactional
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
//        return new OrderResponse(orderRecord.getId(),
//                orderRecord.getMerchantId(),
//                orderRecord.getReceipt(),
//                orderRecord.getAmount(),
//                orderRecord.getOrderStatus(),
//                orderRecord.getAttempts(),
//                orderRecord.getNotes(),
//                orderRecord.getExpiredAt(),
//                null);
        return orderMapper.toOrderResponse(orderRecord);
    }

    @Override
    public OrderResponse getById(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        return orderMapper.toOrderResponse(orderRecord);
    }

    @Override
    public OrderResponse cancel(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        if (orderRecord.getOrderStatus() == OrderStatus.CANCELLED
                || orderRecord.getOrderStatus() == OrderStatus.PAID) {
            log.warn("Order {} is already cancelled", orderId);
            throw new BusinessRuleViolationException("ORDER_CANNOT_CANCEL", "Order " + orderId + " cannot be cancelled");
        }
        orderRecord.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(orderRecord);
        return orderMapper.toOrderResponse(orderRecord);
    }

    @Override
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        List<Payment> paymentList = paymentRepository.findByOrder_Id(orderRecord);
//        return paymentList.stream()
//                .map(paymentMapper::toPaymentResponse)
//                .collect(Collectors.toList());
        return paymentMapper.toPaymentResponseList(paymentList);
    }
}

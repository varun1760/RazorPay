package com.rao.RazorPay.payment.service;

import com.rao.RazorPay.payment.dto.request.CreateOrderRequest;
import com.rao.RazorPay.payment.dto.response.OrderResponse;

import java.util.UUID;

public interface OrderService {
    OrderResponse create(UUID merchantId, CreateOrderRequest request);
}

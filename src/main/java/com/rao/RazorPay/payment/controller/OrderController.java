package com.rao.RazorPay.payment.controller;

import com.rao.RazorPay.merchant.security.MerchantContext;
import com.rao.RazorPay.payment.dto.request.CreateOrderRequest;
import com.rao.RazorPay.payment.dto.response.OrderResponse;
import com.rao.RazorPay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MerchantContext merchantContext;

//    UUID merchantId = UUID.fromString("7f8f1883-3057-42dd-9a09-50130c2c2e5c");

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(merchantContext.getMerchantId(), request));
    }
}

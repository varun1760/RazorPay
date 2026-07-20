package com.rao.RazorPay.payment.controller;

import com.rao.RazorPay.merchant.security.MerchantContext;
import com.rao.RazorPay.payment.dto.request.PaymentInitRequest;
import com.rao.RazorPay.payment.dto.response.PaymentResponse;
import com.rao.RazorPay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MerchantContext merchantContext;

//    UUID merchantId = UUID.fromString("7f8f1883-3057-42dd-9a09-50130c2c2e5c");

    @PostMapping("/init")
    public ResponseEntity<PaymentResponse> initiate(@Valid @RequestBody PaymentInitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiate(merchantContext.getMerchantId(), request));
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<PaymentResponse> capture(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.capture(merchantContext.getMerchantId(), paymentId));
    }
}

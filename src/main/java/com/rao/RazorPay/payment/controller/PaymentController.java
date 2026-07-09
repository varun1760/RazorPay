package com.rao.RazorPay.payment.controller;

import com.rao.RazorPay.payment.dto.request.PaymentInitRequest;
import com.rao.RazorPay.payment.dto.response.PaymentResponse;
import com.rao.RazorPay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/v1/payment")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    UUID merchantId = UUID.fromString("df0e8506-1970-4926-8740-5afd00bc7389");

    @PostMapping("/init")
    public ResponseEntity<PaymentResponse> initiate(@Valid @RequestBody PaymentInitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiate(merchantId, request));
    }
}

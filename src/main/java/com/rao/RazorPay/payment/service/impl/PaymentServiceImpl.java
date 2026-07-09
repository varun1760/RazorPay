package com.rao.RazorPay.payment.service.impl;

import com.rao.RazorPay.common.enums.OrderStatus;
import com.rao.RazorPay.common.enums.PaymentStatus;
import com.rao.RazorPay.common.exception.BusinessRuleViolationException;
import com.rao.RazorPay.common.exception.ResourceNotFoundException;
import com.rao.RazorPay.payment.dto.request.PaymentInitRequest;
import com.rao.RazorPay.payment.dto.response.PaymentResponse;
import com.rao.RazorPay.payment.entity.OrderRecord;
import com.rao.RazorPay.payment.entity.Payment;
import com.rao.RazorPay.payment.gateway.PaymentGatewayRouter;
import com.rao.RazorPay.payment.dto.request.PaymentRequest;
import com.rao.RazorPay.payment.dto.response.PaymentResult;
import com.rao.RazorPay.payment.mapper.PaymentMapper;
import com.rao.RazorPay.payment.repository.OrderRepository;
import com.rao.RazorPay.payment.repository.PaymentRepository;
import com.rao.RazorPay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse initiate(UUID merchantId, PaymentInitRequest request) {
        OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(request.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.orderId()));
        if (orderRecord.getOrderStatus() != OrderStatus.CREATED
                && orderRecord.getOrderStatus() != OrderStatus.ATTEMPTED) {
            log.warn("Order {} in state {} is not payable", request.orderId(), orderRecord.getOrderStatus());
            throw new BusinessRuleViolationException("ORDER_NOT_PAYABLE", "Order in status " + orderRecord.getOrderStatus() + " cannot be accepted");
        }

        orderRecord.setOrderStatus(OrderStatus.ATTEMPTED);
        orderRecord.setAttempts(orderRecord.getAttempts() + 1);

        Payment payment = Payment.builder()
                .merchantId(merchantId)
                .order(orderRecord)
                .amount(orderRecord.getAmount())
                .status(PaymentStatus.CREATED)
                .method(request.method())
                .methodDetails(request.methodDetails())
                .build();
        payment = paymentRepository.save(payment);

        PaymentRequest paymentRequest = new PaymentRequest(
                payment.getId(),
                request.orderId(),
                merchantId,
                orderRecord.getAmount(),
                request.method(),
                request.methodDetails());
        PaymentResult paymentResult = paymentGatewayRouter.initiate(paymentRequest);

        switch (paymentResult) {
            case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationReference());
            case PaymentResult.Failure failure -> {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
        }
        payment =  paymentRepository.save(payment);
        orderRepository.save(orderRecord);
        return paymentMapper.toPaymentResponse(payment);
    }
}

package com.rao.RazorPay.payment.service.impl;

import com.rao.RazorPay.common.enums.OrderStatus;
import com.rao.RazorPay.common.enums.PaymentEvent;
import com.rao.RazorPay.common.enums.PaymentStatus;
import com.rao.RazorPay.common.exception.BusinessRuleViolationException;
import com.rao.RazorPay.common.exception.ResourceNotFoundException;
import com.rao.RazorPay.payment.dto.request.PaymentInitRequest;
import com.rao.RazorPay.payment.dto.request.PaymentRequest;
import com.rao.RazorPay.payment.dto.response.PaymentResponse;
import com.rao.RazorPay.payment.dto.response.PaymentResult;
import com.rao.RazorPay.payment.entity.OrderRecord;
import com.rao.RazorPay.payment.entity.Payment;
import com.rao.RazorPay.payment.gateway.PaymentGatewayRouter;
import com.rao.RazorPay.payment.mapper.PaymentMapper;
import com.rao.RazorPay.payment.repository.OrderRepository;
import com.rao.RazorPay.payment.repository.PaymentRepository;
import com.rao.RazorPay.payment.service.PaymentService;
import com.rao.RazorPay.payment.statemachine.PaymentTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;
    private final PaymentTransitionService  paymentTransitionService;

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
                .idempotencyKey(UUID.randomUUID().toString())
                .build();
        payment = paymentRepository.save(payment);

        PaymentRequest paymentRequest = new PaymentRequest(
                payment.getId(),
                request.orderId(),
                merchantId,
                orderRecord.getAmount(),
                request.method(),
                request.methodDetails());

        paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_ATTEMPT);
        PaymentResult paymentResult = paymentGatewayRouter.initiate(paymentRequest);

        switch (paymentResult) {
            case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationReference());
            case PaymentResult.Failure failure -> {
//                payment.setStatus(PaymentStatus.FAILED);
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            case PaymentResult.Success success -> {
                log.warn("Invalid state");
                return null;
            }
        }
        payment =  paymentRepository.save(payment);
        orderRepository.save(orderRecord);
        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    public PaymentResponse capture(UUID merchantId, UUID paymentId) {

        Payment payment = paymentRepository.findByIdAndMerchantId(paymentId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
//        payment.setStatus(PaymentStatus.CAPTURING);
        paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
        PaymentResult paymentResult = paymentGatewayRouter.capture(payment.getMethod(), paymentId);

        if (paymentResult instanceof PaymentResult.Success) {
//            payment.setStatus(PaymentStatus.CAPTURED);
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
            payment.setCapturedAt(LocalDateTime.now());
            log.info("Payment {} has been captured", paymentId);
        } else if (paymentResult instanceof PaymentResult.Failure failure) {
//            payment.setStatus(PaymentStatus.AUTHORIZED);
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
            payment.setErrorCode(failure.errorCode());
            payment.setErrorDescription(failure.errorDescription());
            log.info("Payment {} has been failed", paymentId);
        }

        payment = paymentRepository.save(payment);

        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional
    public void resolveAuthorization(
            UUID paymentId, boolean approved, String bankReference, String errorCode, String errorDescription) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
        if (payment.getStatus() != PaymentStatus.AUTHORIZING) {
            log.warn("Payment is not in Authorizing state {} for payment {}", payment.getStatus(), paymentId);
            return;
        }
        OrderRecord orderRecord = payment.getOrder();
        if (approved) {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankReference);
            payment.setAuthorizedAt(LocalDateTime.now());
            log.info("Payment {} has been authorized", paymentId);

            // Auto-capture
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
            PaymentResult paymentResult = paymentGatewayRouter.capture(payment.getMethod(), paymentId);

            if (paymentResult instanceof PaymentResult.Success) {
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                orderRecord.setOrderStatus(OrderStatus.PAID);
            } else if (paymentResult instanceof PaymentResult.Failure) {
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
                payment.setErrorCode(errorCode);
                payment.setErrorDescription(errorDescription);
            }
        } else {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
        }

        paymentRepository.save(payment);
        orderRepository.save(orderRecord);

    }
}

package com.rao.RazorPay.payment.repository;

import com.rao.RazorPay.payment.entity.OrderRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends CrudRepository<OrderRecord, UUID> {
    boolean existsByMerchantIdAndReceipt(UUID merchantId, String receipt);

    Optional<OrderRecord> findByIdAndMerchantId(UUID orderId, UUID merchantId);
}

package com.rao.RazorPay.payment.repository;

import com.rao.RazorPay.payment.entity.OrderRecord;
import com.rao.RazorPay.payment.entity.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends CrudRepository<Payment, UUID> {
    List<Payment> findByOrder_Id(OrderRecord orderRecord);
}

package com.rao.RazorPay.merchant.repository;

import com.rao.RazorPay.merchant.entity.ApiKey;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ApiKeyRepository extends CrudRepository<ApiKey, UUID> {
    List<ApiKey> findByMerchant_Id(UUID merchantId);
}

package com.rao.RazorPay.merchant.service;

import com.rao.RazorPay.merchant.dto.request.CreateApiKeyRequest;
import com.rao.RazorPay.merchant.dto.response.ApiKeyCreateResponse;
import com.rao.RazorPay.merchant.dto.response.ApiKeyResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {
    ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request);

    List<ApiKeyResponse> listByMerchant(UUID merchantId);

    void revoke(UUID merchantId, UUID keyId);

    ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId);
}

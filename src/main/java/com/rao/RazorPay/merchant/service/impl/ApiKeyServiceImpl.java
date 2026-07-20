package com.rao.RazorPay.merchant.service.impl;

import com.rao.RazorPay.common.exception.ResourceNotFoundException;
import com.rao.RazorPay.common.util.RandomizerUtil;
import com.rao.RazorPay.merchant.dto.request.CreateApiKeyRequest;
import com.rao.RazorPay.merchant.dto.response.ApiKeyCreateResponse;
import com.rao.RazorPay.merchant.dto.response.ApiKeyResponse;
import com.rao.RazorPay.merchant.entity.ApiKey;
import com.rao.RazorPay.merchant.entity.Merchant;
import com.rao.RazorPay.merchant.mapper.ApiKeyMapper;
import com.rao.RazorPay.merchant.repository.ApiKeyRepository;
import com.rao.RazorPay.merchant.repository.MerchantRepository;
import com.rao.RazorPay.merchant.service.ApiKeyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyMapper apiKeyMapper;
    private final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));
        String keyId = "rzp_" + request.environment().name().toLowerCase() + "_" + RandomizerUtil.randomBase64(24);
        String rawSecret = RandomizerUtil.randomBase64(48);

        ApiKey apiKey = ApiKey.builder()
                .keyId(keyId)
                .keySecretHash(BCRYPT.encode(rawSecret))
                .environment(request.environment())
                .merchant(merchant)
                .build();

        apiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(
                apiKey.getId(),
                apiKey.getKeyId(),
                rawSecret,
                apiKey.getEnvironment()
        );
    }

    @Override
    public List<ApiKeyResponse> listByMerchant(UUID merchantId) {
//        return apiKeyRepository.findByMerchant_Id(merchantId)
//                .stream()
//                .map(apiKey ->
//                        new ApiKeyResponse(
//                                apiKey.getId(),
//                                apiKey.getKeyId(),
//                                apiKey.getEnvironment(),
//                                apiKey.isEnabled(),
//                                apiKey.getLastUsedAt(),
//                                null
//                ))
//                .toList();
        return apiKeyMapper.toApiKeyResponseList(apiKeyRepository.findByMerchant_Id(merchantId));
    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey key = apiKeyRepository.findById(keyId)
                .filter(apiKey -> apiKey.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey", keyId));
        key.setEnabled(false);
        apiKeyRepository.save(key);     // not required if transactional is used, but we can explicitly save this by choice
    }

    @Override
    @Transactional
    public ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(key -> key.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey", keyId));

        if (!apiKey.isEnabled()) {
            throw new IllegalStateException("Cannot rotate a revoked API key");
        }

        String newRawSecret = RandomizerUtil.randomBase64(48);

        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(BCRYPT.encode(newRawSecret));
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24));
        apiKey =  apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(apiKey.getId(), apiKey.getKeyId(), newRawSecret, apiKey.getEnvironment());
    }
}

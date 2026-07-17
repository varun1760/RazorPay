package com.rao.RazorPay.vault.service.impl;

import com.rao.RazorPay.common.entity.Money;
import com.rao.RazorPay.common.enums.CardBrand;
import com.rao.RazorPay.common.exception.ResourceNotFoundException;
import com.rao.RazorPay.common.util.RandomizerUtil;
import com.rao.RazorPay.payment.dto.request.PaymentProcessorRequest;
import com.rao.RazorPay.payment.dto.response.PaymentProcessorResponse;
import com.rao.RazorPay.payment.processor.PaymentProcessorRouter;
import com.rao.RazorPay.vault.config.VaultEncryptionConfig;
import com.rao.RazorPay.vault.dto.request.TokenizeRequest;
import com.rao.RazorPay.vault.dto.response.TokenizeResponse;
import com.rao.RazorPay.vault.entity.CardToken;
import com.rao.RazorPay.vault.entity.VaultCard;
import com.rao.RazorPay.vault.repository.CardTokenRepository;
import com.rao.RazorPay.vault.repository.VaultCardRepository;
import com.rao.RazorPay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VaultServiceImpl implements VaultService {

    private final VaultCardRepository vaultCardRepository;
    private final CardTokenRepository cardTokenRepository;
    private final BytesEncryptor dekEncryptor;
    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    @Transactional
    public TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId) {

        String lastFour = request.pan().substring(request.pan().length() - 4);
        String bin = request.pan().substring(0, 6);
        CardBrand cardBrand = detectBrand(request.pan());

        byte[] dek = KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan = VaultEncryptionConfig
                .panEncryptor(dek)
                .encrypt(request.pan().getBytes(StandardCharsets.UTF_8));
        byte[] encryptedDek = dekEncryptor.encrypt(dek);

        VaultCard savedVaultCard =  vaultCardRepository.save(VaultCard.builder()
                .brand(cardBrand)
                .expiryMonth(request.expiryMonth().toString())
                .expiryYear(request.expiryYear().toString())
                .bin(bin)
                .lastFour(lastFour)
                .encryptedDek(encryptedDek)
                .encryptedPan(encryptedPan)
                .build());

        String token = "token_" + RandomizerUtil.randomBase64(32);

        cardTokenRepository.save(CardToken.builder()
                .token(token)
                .vaultCard(savedVaultCard)
                .customer(request.customerId())
                .merchant(merchantId)
                .build());

        return new TokenizeResponse(token, lastFour, cardBrand, request.expiryMonth(),  request.expiryYear());
    }

    private CardBrand detectBrand(String pan) {
        if (pan.startsWith("4")) {
            return CardBrand.VISA;
        }
        if (pan.startsWith("5") || pan.startsWith("2")) {
            return CardBrand.MASTERCARD;
        }
        if (pan.startsWith("37") || pan.startsWith("34")) {
            return CardBrand.AMEX;
        }
        return CardBrand.RUPAY;
    }

    @Override
    public PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails) {

        CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token)
                .orElseThrow(() -> new ResourceNotFoundException("CardToken", token));

        VaultCard vaultCard = cardToken.getVaultCard();
        byte[] panBytes = null;
        try {
            byte[] dek = dekEncryptor.decrypt(vaultCard.getEncryptedDek());
            panBytes = VaultEncryptionConfig.panEncryptor(dek).decrypt(vaultCard.getEncryptedPan());

            String pan = new String(panBytes, StandardCharsets.UTF_8);
            String expiry = vaultCard.getExpiryMonth() + "/" + vaultCard.getExpiryYear();

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.card(
                    paymentId, amount, pan, expiry, methodDetails
            );
            PaymentProcessorResponse response = paymentProcessorRouter.charge(paymentProcessorRequest);
            log.info("Vault charge registered, token: {}****", token.substring(0, 4));

            return response;
        } catch (Exception e) {
            log.warn("Vault charge failed, token: {}****", token.substring(0, 4));
            return new PaymentProcessorResponse.Failure("VAULT_CHARGE_FAILED", e.getMessage());
        } finally {
            if (panBytes != null) {
                Arrays.fill(panBytes, (byte) 0);
            }
        }
    }
}

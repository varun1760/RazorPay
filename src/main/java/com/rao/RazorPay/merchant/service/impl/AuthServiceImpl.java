package com.rao.RazorPay.merchant.service.impl;

import com.rao.RazorPay.common.enums.MerchantStatus;
import com.rao.RazorPay.common.enums.UserRole;
import com.rao.RazorPay.common.exception.DuplicateResourceException;
import com.rao.RazorPay.merchant.dto.request.MerchantSignupRequest;
import com.rao.RazorPay.merchant.dto.response.MerchantResponse;
import com.rao.RazorPay.merchant.entity.AppUser;
import com.rao.RazorPay.merchant.entity.Merchant;
import com.rao.RazorPay.merchant.repository.AppUserRepository;
import com.rao.RazorPay.merchant.repository.MerchantRepository;
import com.rao.RazorPay.merchant.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;

    @Override
    @Transactional
    public MerchantResponse signup(MerchantSignupRequest request) {
        if (merchantRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL",
                    "Merchant with email already exists: " +  request.email());
        }

        Merchant merchant = Merchant.builder()
                .email(request.email())
                .name(request.name())
                .businessName(request.businessName())
                .businessType(request.businessType())
                .status(MerchantStatus.PENDING_KYC)
                .build();

        merchantRepository.save(merchant);

        AppUser appUser = AppUser.builder()
                .email(request.email())
                .passwordHash(request.password())
                .merchant(merchant)
                .role(UserRole.OWNER)
                .build();
        appUserRepository.save(appUser);

        return new MerchantResponse(merchant.getId(), merchant.getName(), merchant.getEmail(),
                merchant.getBusinessName(), merchant.getBusinessType(), merchant.getStatus());
    }

}

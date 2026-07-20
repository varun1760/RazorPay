package com.rao.RazorPay.merchant.service.impl;

import com.rao.RazorPay.common.enums.MerchantStatus;
import com.rao.RazorPay.common.enums.UserRole;
import com.rao.RazorPay.common.exception.DuplicateResourceException;
import com.rao.RazorPay.common.exception.ResourceNotFoundException;
import com.rao.RazorPay.merchant.dto.request.LoginRequest;
import com.rao.RazorPay.merchant.dto.request.MerchantSignupRequest;
import com.rao.RazorPay.merchant.dto.response.LoginResponse;
import com.rao.RazorPay.merchant.dto.response.MerchantResponse;
import com.rao.RazorPay.merchant.entity.AppUser;
import com.rao.RazorPay.merchant.entity.Merchant;
import com.rao.RazorPay.merchant.mapper.MerchantMapper;
import com.rao.RazorPay.merchant.repository.AppUserRepository;
import com.rao.RazorPay.merchant.repository.MerchantRepository;
import com.rao.RazorPay.merchant.security.JwtUtil;
import com.rao.RazorPay.merchant.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;
    private final MerchantMapper merchantMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public MerchantResponse signup(MerchantSignupRequest request) {
        if (merchantRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL",
                    "Merchant with email already exists: " +  request.email());
        }

//        Merchant merchant = Merchant.builder()
//                .email(request.email())
//                .name(request.name())
//                .businessName(request.businessName())
//                .businessType(request.businessType())
//                .status(MerchantStatus.PENDING_KYC)
//                .build();
        Merchant merchant = merchantMapper.toEntityFromMerchantSignupRequest(request);
        merchant.setStatus(MerchantStatus.PENDING_KYC);

        merchantRepository.save(merchant);

        AppUser appUser = AppUser.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .merchant(merchant)
                .role(UserRole.OWNER)
                .build();
        appUserRepository.save(appUser);

//        return new MerchantResponse(merchant.getId(), merchant.getName(), merchant.getEmail(),
//                merchant.getBusinessName(), merchant.getBusinessType(), merchant.getStatus());
        return merchantMapper.toMerchantResponse(merchant);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        AppUser appUser = appUserRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.email()));
        String token = jwtUtil.generateAccessToken(request.email(), appUser.getMerchant().getId(), appUser.getRole().toString());
        return new LoginResponse(token);
    }

}

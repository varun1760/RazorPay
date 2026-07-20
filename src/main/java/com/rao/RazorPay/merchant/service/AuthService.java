package com.rao.RazorPay.merchant.service;

import com.rao.RazorPay.merchant.dto.request.LoginRequest;
import com.rao.RazorPay.merchant.dto.request.MerchantSignupRequest;
import com.rao.RazorPay.merchant.dto.response.LoginResponse;
import com.rao.RazorPay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {

    MerchantResponse signup(MerchantSignupRequest request);

    LoginResponse login(@Valid LoginRequest request);
}

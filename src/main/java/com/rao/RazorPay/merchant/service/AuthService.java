package com.rao.RazorPay.merchant.service;

import com.rao.RazorPay.merchant.dto.request.MerchantSignupRequest;
import com.rao.RazorPay.merchant.dto.response.MerchantResponse;

public interface AuthService {

    MerchantResponse signup(MerchantSignupRequest request);

}

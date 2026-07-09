package com.rao.RazorPay.merchant.mapper;

import com.rao.RazorPay.merchant.dto.request.MerchantSignupRequest;
import com.rao.RazorPay.merchant.dto.response.MerchantResponse;
import com.rao.RazorPay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantMapper {

    Merchant toEntityFromMerchantSignupRequest(MerchantSignupRequest merchantSignupRequest);

    MerchantResponse toMerchantResponse(Merchant merchant);
}

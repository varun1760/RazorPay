package com.rao.RazorPay.merchant.mapper;

import com.rao.RazorPay.merchant.dto.response.ApiKeyResponse;
import com.rao.RazorPay.merchant.entity.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyMapper {

    List<ApiKeyResponse>  toApiKeyResponseList(List<ApiKey> apiKeyList);
}

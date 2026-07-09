package com.rao.RazorPay.payment.mapper;

import com.rao.RazorPay.payment.dto.response.OrderResponse;
import com.rao.RazorPay.payment.entity.OrderRecord;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    OrderResponse toOrderResponse(OrderRecord orderRecord);
}

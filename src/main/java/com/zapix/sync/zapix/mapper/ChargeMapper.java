package com.zapix.sync.zapix.mapper;

import com.zapix.sync.zapix.dto.ChargeResponse;
import com.zapix.sync.zapix.model.Charge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChargeMapper {
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "subscriptionId", source = "subscription.id")
    ChargeResponse toResponse(Charge charge);
}

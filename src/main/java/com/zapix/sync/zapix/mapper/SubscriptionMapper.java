package com.zapix.sync.zapix.mapper;

import com.zapix.sync.zapix.dto.SubscriptionCreateRequest;
import com.zapix.sync.zapix.dto.SubscriptionResponse;
import com.zapix.sync.zapix.dto.SubscriptionUpdateRequest;
import com.zapix.sync.zapix.model.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "nextDueDate", source = "firstDueDate")
    @Mapping(target = "billingDay", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Subscription toEntity(SubscriptionCreateRequest request);

    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    SubscriptionResponse toResponse(Subscription subscription);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "nextDueDate", ignore = true)
    @Mapping(target = "billingDay", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(SubscriptionUpdateRequest request, @MappingTarget Subscription subscription);
}

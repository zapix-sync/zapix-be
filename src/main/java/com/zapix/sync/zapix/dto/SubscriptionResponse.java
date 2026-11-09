package com.zapix.sync.zapix.dto;

import com.zapix.sync.zapix.enums.BillingCycle;
import com.zapix.sync.zapix.enums.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionResponse(
        UUID id,
        UUID companyId,
        UUID customerId,
        String customerName,
        String description,
        BigDecimal amount,
        BillingCycle billingCycle,
        LocalDate nextDueDate,
        SubscriptionStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}

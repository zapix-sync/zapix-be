package com.zapix.sync.zapix.dto;

import com.zapix.sync.zapix.enums.ChargeStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ChargeResponse(
        UUID id,
        UUID companyId,
        UUID customerId,
        String customerName,
        UUID subscriptionId,
        String description,
        BigDecimal amount,
        LocalDate dueDate,
        ChargeStatus status,
        Instant paidAt,
        Instant createdAt,
        Instant updatedAt
) {
}

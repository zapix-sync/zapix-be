package com.zapix.sync.zapix.dto;

import com.zapix.sync.zapix.enums.BillingCycle;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record SubscriptionUpdateRequest(
        @NotBlank(message = "description is required")
        @Size(max = 150, message = "description must have at most 150 characters")
        String description,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "amount must have at most 10 integer digits and 2 decimal places")
        BigDecimal amount,

        @NotNull(message = "billingCycle is required")
        BillingCycle billingCycle
) {
}

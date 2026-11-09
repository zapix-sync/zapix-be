package com.zapix.sync.zapix.dto;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        UUID companyId,
        String name,
        String document,
        String email,
        String phone,
        boolean whatsappOptIn,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}

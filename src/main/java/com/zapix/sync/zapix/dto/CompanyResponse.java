package com.zapix.sync.zapix.dto;

import java.time.Instant;
import java.util.UUID;

public record CompanyResponse(
        UUID id,
        String name,
        String document,
        String email,
        String phone,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}

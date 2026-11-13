package com.zapix.sync.zapix.integration.pix;

import java.math.BigDecimal;
import java.util.UUID;

public record PixChargeRequest(
        UUID chargeId,
        BigDecimal amount,
        String description
) {
}

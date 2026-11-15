package com.zapix.sync.zapix.integration.pix;

import java.time.Instant;

public record PixChargeResult(
        String gatewayChargeId,
        String paymentUrl,
        String copyPaste,
        String qrCodeUrl,
        Instant expiresAt
) {
}

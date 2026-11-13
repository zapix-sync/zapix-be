package com.zapix.sync.zapix.integration.pix;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "zapix.pix.gateway", havingValue = "fake", matchIfMissing = true)
public class FakePixGateway implements PixGateway {
    @Override
    public PixChargeResult createCharge(PixChargeRequest request) {
        String gatewayChargeId = "fake_" + UUID.randomUUID();

        return new PixChargeResult(
                gatewayChargeId,
                "FAKE-PIX-NOT-A-REAL-PAYMENT-" + request.amount(),
                "https://example.com/fake-qr/" + gatewayChargeId + ".png",
                Instant.now().plus(1, ChronoUnit.DAYS));
    }
}

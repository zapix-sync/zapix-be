package com.zapix.sync.zapix.integration.pix;

public interface PixGateway {
    PixChargeResult createCharge(PixChargeRequest request);
}

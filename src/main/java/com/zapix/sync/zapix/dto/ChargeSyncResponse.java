package com.zapix.sync.zapix.dto;

import com.zapix.sync.zapix.enums.ChargeStatus;

public record ChargeSyncResponse(
        String gatewayStatus,
        String gatewayPaymentStatus,
        ChargeStatus chargeStatus
) {
}

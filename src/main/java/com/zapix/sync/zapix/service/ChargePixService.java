package com.zapix.sync.zapix.service;

import com.zapix.sync.zapix.dto.ChargeResponse;
import com.zapix.sync.zapix.enums.ChargeStatus;
import com.zapix.sync.zapix.exception.BusinessRuleException;
import com.zapix.sync.zapix.exception.ResourceNotFoundException;
import com.zapix.sync.zapix.integration.pix.PixChargeRequest;
import com.zapix.sync.zapix.integration.pix.PixChargeResult;
import com.zapix.sync.zapix.integration.pix.PixGateway;
import com.zapix.sync.zapix.mapper.ChargeMapper;
import com.zapix.sync.zapix.model.Charge;
import com.zapix.sync.zapix.repository.ChargeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChargePixService {

    private final ChargeRepository chargeRepository;
    private final PixGateway pixGateway;
    private final ChargeMapper chargeMapper;

    @Transactional
    public ChargeResponse generatePix(UUID companyId, UUID chargeId) {
        Charge charge = chargeRepository.findByIdAndCompanyId(chargeId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Charge not found: " + chargeId));

        if (charge.getStatus() != ChargeStatus.PENDING && charge.getStatus() != ChargeStatus.OVERDUE) {
            throw new BusinessRuleException(
                    "Pix can only be generated for pending or overdue charges (current status: "
                            + charge.getStatus() + ")");
        }

        if (!hasValidPix(charge)) {
            PixChargeResult result = pixGateway.createCharge(
                    new PixChargeRequest(charge.getId(), charge.getAmount(), charge.getDescription()));

            charge.setGatewayChargeId(result.gatewayChargeId());
            charge.setPixCopyPaste(result.copyPaste());
            charge.setPixQrCodeUrl(result.qrCodeUrl());
            charge.setPixExpiresAt(result.expiresAt());
        }

        return chargeMapper.toResponse(chargeRepository.saveAndFlush(charge));
    }

    private boolean hasValidPix(Charge charge) {
        return charge.getGatewayChargeId() != null
                && charge.getPixExpiresAt() != null
                && charge.getPixExpiresAt().isAfter(Instant.now());
    }
}

package com.zapix.sync.zapix.service;

import java.time.Instant;
import java.util.UUID;

import com.zapix.sync.zapix.enums.ChargeStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.zapix.sync.zapix.dto.ChargeSyncResponse;
import com.zapix.sync.zapix.exception.BusinessRuleException;
import com.zapix.sync.zapix.exception.ResourceNotFoundException;
import com.zapix.sync.zapix.integration.pix.PixGatewayException;
import com.zapix.sync.zapix.model.Charge;
import com.zapix.sync.zapix.repository.ChargeRepository;

import lombok.RequiredArgsConstructor;

@Service
@ConditionalOnProperty(name = "zapix.pix.gateway", havingValue = "stripe")
@RequiredArgsConstructor
public class ChargeSyncService {
    private final ChargeRepository  chargeRepository;
    private final ChargeService chargeService;

    @Value("${stripe.secret-key}")
    private String secretKey;

    public ChargeSyncResponse sync(UUID companyId, UUID chargeId) {
        Charge charge = chargeRepository.findByIdAndCompanyId(chargeId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Charge not found: " + chargeId));

        String sessionsId = charge.getGatewayChargeId();

        if (sessionsId == null) {
            throw new BusinessRuleException("Charge has no payment link yet. Generate one with POST /pix first");
        }

        Session session;

        try {
            session = Session.retrieve(sessionsId, RequestOptions.builder().setApiKey(secretKey).build());
        } catch (StripeException ex) {
            throw new PixGatewayException("Failed to retrieve the Stripe Checkout Session", ex);
        }

        if ("paid".equals(session.getPaymentStatus())) {
            chargeService.confirmPayment(sessionsId, Instant.now());
        }

        ChargeStatus current = chargeRepository.findByIdAndCompanyId(chargeId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Charge not found: " + chargeId))
                .getStatus();

        return new ChargeSyncResponse(session.getStatus(), session.getPaymentStatus(), current);
    }
}

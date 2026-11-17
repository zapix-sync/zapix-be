package com.zapix.sync.zapix.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(name = "zapix.pix.gateway", havingValue = "stripe")
@RequiredArgsConstructor
public class ChargeSyncService {

    private static final List<ChargeStatus> OPEN_STATUSES =
            List.of(ChargeStatus.PENDING, ChargeStatus.OVERDUE);

    private final ChargeRepository chargeRepository;
    private final ChargeService chargeService;

    @Value("${stripe.secret-key}")
    private String secretKey;

    public ChargeSyncResponse sync(UUID companyId, UUID chargeId) {
        Charge charge = chargeRepository.findByIdAndCompanyId(chargeId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Charge not found: " + chargeId));

        return check(charge);
    }

    public int reconcilePending() {
        Instant since = Instant.now().minus(1, ChronoUnit.HOURS);

        List<Charge> candidates = chargeRepository
                .findAllByStatusInAndGatewayChargeIdIsNotNullAndPixExpiresAtAfter(OPEN_STATUSES, since);

        int recovered = 0;

        for (Charge charge : candidates) {
            if (!charge.getGatewayChargeId().startsWith("cs_")) {
                continue;
            }

            try {
                if (check(charge).chargeStatus() == ChargeStatus.PAID) {
                    recovered++;
                }
            } catch (Exception e) {
                log.error("Failed to reconcile charge {}", charge.getId(), e);
            }
        }

        log.debug("Reconciliation checked {} open charges, {} recovered", candidates.size(), recovered);
        return recovered;
    }

    private ChargeSyncResponse check(Charge charge) {
        String sessionId = charge.getGatewayChargeId();
        if (sessionId == null) {
            throw new BusinessRuleException("Charge has no payment link yet. Generate one with POST /pix first");
        }

        Session session;
        try {
            session = Session.retrieve(sessionId, RequestOptions.builder().setApiKey(secretKey).build());
        } catch (StripeException e) {
            throw new PixGatewayException("Failed to retrieve the Stripe Checkout Session", e);
        }

        if ("paid".equals(session.getPaymentStatus())) {
            chargeService.confirmPayment(sessionId, Instant.now());
        }

        ChargeStatus current = chargeRepository.findById(charge.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Charge not found: " + charge.getId()))
                .getStatus();

        return new ChargeSyncResponse(session.getStatus(), session.getPaymentStatus(), current);
    }
}
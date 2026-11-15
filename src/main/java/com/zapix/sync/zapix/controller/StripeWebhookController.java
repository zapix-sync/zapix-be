package com.zapix.sync.zapix.controller;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.zapix.sync.zapix.exception.ResourceNotFoundException;
import com.zapix.sync.zapix.service.ChargeService;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Hidden
@RestController
@RequestMapping("/api/webhooks/stripe")
@ConditionalOnProperty(name = "zapix.pix.gateway", havingValue = "stripe")
@RequiredArgsConstructor
public class StripeWebhookController {
    private final  ChargeService chargeService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void handle(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException ex) {
            log.warn("Stripe webhook signature verification failed");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Stripe signature");
        }

        log.info("Received Stripe event {} ({})", event.getType(), event.getId());

        switch (event.getType()) {
            case "payment_intent.succeeded" -> handlePaymentSucceeded(event);
            case "checkout.session.async_payment_succeeded" -> handleCheckoutPaymentSucceeded(event);
            case "checkout.session.completed" -> handleCheckoutSessionCompleted(event);
            case "payment_intent.payment_failed" -> log.info(
                    "Pix payment failed or expired (event {}). The charge stays open, a new link can be generated",
                    event.getId());
            default -> log.debug("Ignoring Stripe event {}", event.getType());
        }
    }

    private void handlePaymentSucceeded(Event event) {
        PaymentIntent paymentIntent = extractPaymentIntent(event);
        String chargeId = paymentIntent.getMetadata().get("charge_id");

        if (chargeId == null) {
            log.warn("PaymentIntent {} has no charge_id metadata, ignoring", paymentIntent.getId());
            return;
        }

        try {
            chargeService.confirmPaymentByChargeId(UUID.fromString(chargeId), Instant.ofEpochSecond(event.getCreated()));
        } catch (ResourceNotFoundException e) {
            log.warn("Stripe payment {} refers to unknown charge {}", paymentIntent.getId(), chargeId);
        }
    }

    private void handleCheckoutPaymentSucceeded(Event event) {
        Session session = extractCheckoutSession(event);
        confirmCheckoutSessionPayment(session, event);
    }

    private void handleCheckoutSessionCompleted(Event event) {
        Session session = extractCheckoutSession(event);
        if (!"paid".equals(session.getPaymentStatus())) {
            log.info("Checkout Session {} completed with payment status {}; waiting for async payment event",
                    session.getId(), session.getPaymentStatus());
            return;
        }

        confirmCheckoutSessionPayment(session, event);
    }

    private void confirmCheckoutSessionPayment(Session session, Event event) {
        String chargeId = session.getClientReferenceId();

        if (chargeId == null) {
            log.warn("Checkout Session {} has no client_reference_id, ignoring", session.getId());
            return;
        }

        try {
            chargeService.confirmPaymentByChargeId(
                    UUID.fromString(chargeId),
                    Instant.ofEpochSecond(event.getCreated()));
            log.info("Processed successful payment for charge {} from Stripe event {}",
                    chargeId, event.getId());
        } catch (ResourceNotFoundException e) {
            log.warn("Stripe Checkout Session {} refers to unknown charge {}", session.getId(), chargeId);
        }
    }

    private PaymentIntent extractPaymentIntent(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        Optional<StripeObject> object = deserializer.getObject();
        if (object.isPresent()) {
            return (PaymentIntent) object.get();
        }

        try {
            return (PaymentIntent) deserializer.deserializeUnsafe();
        } catch (EventDataObjectDeserializationException e) {
            throw new IllegalStateException("Could not read Stripe event " + event.getId(), e);
        }
    }

    private Session extractCheckoutSession(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        Optional<StripeObject> object = deserializer.getObject();
        if (object.isPresent()) {
            return (Session) object.get();
        }

        try {
            return (Session) deserializer.deserializeUnsafe();
        } catch (EventDataObjectDeserializationException e) {
            throw new IllegalStateException("Could not read Stripe event " + event.getId(), e);
        }
    }
}

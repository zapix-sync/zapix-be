package com.zapix.sync.zapix.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.zapix.sync.zapix.service.ChargeService;

class StripeWebhookControllerTest {
    private static final String WEBHOOK_SECRET = "whsec_test_secret";

    @Test
    void confirmsChargeFromAsyncCheckoutPaymentSucceededEvent() throws Exception {
        UUID chargeId = UUID.randomUUID();
        long createdAt = 1_794_754_800L;
        String payload = checkoutEventPayload(
                "checkout.session.async_payment_succeeded", createdAt, chargeId, null);

        assertChargeConfirmed(chargeId, payload);
    }

    @Test
    void confirmsChargeFromCompletedCheckoutWhenPaymentIsAlreadyPaid() throws Exception {
        UUID chargeId = UUID.randomUUID();
        long createdAt = 1_794_754_800L;
        String payload = checkoutEventPayload("checkout.session.completed", createdAt, chargeId, "paid");

        assertChargeConfirmed(chargeId, payload);
    }

    private String checkoutEventPayload(String eventType, long createdAt, UUID chargeId, String paymentStatus) {
        String paymentStatusField = paymentStatus == null
                ? ""
                : """
                          "payment_status": "%s",
                        """.formatted(paymentStatus);
        return """
                {
                  "id": "evt_test",
                  "object": "event",
                  "api_version": "2026-07-29.dahlia",
                  "created": %d,
                  "data": {
                    "object": {
                      "id": "cs_test_checkout",
                      "object": "checkout.session",
                      %s
                      "client_reference_id": "%s"
                    }
                  },
                  "livemode": false,
                  "pending_webhooks": 1,
                  "type": "%s"
                }
                """.formatted(createdAt, paymentStatusField, chargeId, eventType);
    }

    private void assertChargeConfirmed(UUID chargeId, String payload) throws Exception {
        ChargeService chargeService = mock(ChargeService.class);
        StripeWebhookController controller = new StripeWebhookController(chargeService);
        ReflectionTestUtils.setField(controller, "webhookSecret", WEBHOOK_SECRET);
        controller.handle(payload, signatureHeader(payload, Instant.now().getEpochSecond()));
        verify(chargeService).confirmPaymentByChargeId(eq(chargeId), any());
    }

    private String signatureHeader(String payload, long timestamp) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(WEBHOOK_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signature = mac.doFinal((timestamp + "." + payload).getBytes(StandardCharsets.UTF_8));
        return "t=" + timestamp + ",v1=" + HexFormat.of().formatHex(signature);
    }
}

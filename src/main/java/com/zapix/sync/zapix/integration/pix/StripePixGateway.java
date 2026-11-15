package com.zapix.sync.zapix.integration.pix;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;

@Component
@ConditionalOnProperty(name = "zapix.pix.gateway", havingValue = "stripe")
public class StripePixGateway implements PixGateway {
    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.success-url:http://localhost:3000/payment/success}")
    private String successUrl;

    @Override
    public PixChargeResult createCharge(PixChargeRequest request) {
        long amountIntCents = request.amount().movePointRight(2).longValueExact();
        String chargeId = request.chargeId().toString();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addAllowedPaymentMethodType(SessionCreateParams.AllowedPaymentMethodType.PIX)
                .setSuccessUrl(successUrl)
                .setClientReferenceId(chargeId)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("brl")
                                .setUnitAmount(amountIntCents)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(request.description())
                                        .build())
                                .build())
                        .build())
                .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
                        .putMetadata("charge_id", chargeId)
                        .build())
                .build();

        try {
            Session session = Session.create(params, RequestOptions.builder().setApiKey(secretKey).build());

            return new PixChargeResult(
                    session.getId(),
                    session.getUrl(),
                    null,
                    null,
                    Instant.ofEpochSecond(session.getExpiresAt()));
        } catch (StripeException e) {
            throw new PixGatewayException("Failed to create the Stripe Checkout Session", e);
        }
    }
}

package com.zapix.sync.zapix.integration.whatsapp;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "zapix.whatsapp.gateway", havingValue = "evolution")
public class EvolutionWhatsAppGateway implements WhatsAppGateway {

    private final RestClient client;

    public EvolutionWhatsAppGateway(
            @Value("${evolution.base-url}") String baseUrl,
            @Value("${evolution.api-key}") String apiKey) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(15));

        this.client = RestClient.builder()
                .requestFactory(factory)
                .baseUrl(baseUrl)
                .defaultHeader("apikey", apiKey)
                .build();
    }

    @Override
    public WhatsAppSendResult sendText(WhatsAppMessage message) {
        String number = normalizePhone(message.phone());

        try {
            SendResponse response = client.post()
                    .uri("/message/sendText/{instance}", message.instanceName())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("number", number, "text", message.text()))
                    .retrieve()
                    .body(SendResponse.class);

            String messageId = (response != null && response.key() != null) ? response.key().id() : null;
            log.info("WhatsApp message sent via instance {} to {} (id={})",
                    message.instanceName(), number, messageId);

            return new WhatsAppSendResult(messageId);
        } catch (RestClientException e) {
            throw new WhatsAppGatewayException("Failed to send WhatsApp message via Evolution API", e);
        }
    }

    // Keeps only digits and adds Brazil's country code when it is missing
    private String normalizePhone(String phone) {
        String digits = phone == null ? "" : phone.replaceAll("\\D", "");

        if (digits.length() == 10 || digits.length() == 11) {
            digits = "55" + digits;
        }
        if (digits.length() < 12) {
            throw new WhatsAppGatewayException("Invalid phone number: " + phone);
        }
        return digits;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SendResponse(Key key) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        private record Key(String id) {
        }
    }
}
package com.zapix.sync.zapix.integration.whatsapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "zapix.whatsapp.gateway", havingValue = "fake", matchIfMissing = true)
public class FakeWhatsAppGateway implements WhatsAppGateway {

    @Override
    public WhatsAppSendResult sendText(WhatsAppMessage message) {
        log.info("[FAKE WhatsApp] instance={} to={} text={}",
                message.instanceName(), message.phone(), message.text());
        return new WhatsAppSendResult("fake-wa-" + UUID.randomUUID().toString());
    }
}

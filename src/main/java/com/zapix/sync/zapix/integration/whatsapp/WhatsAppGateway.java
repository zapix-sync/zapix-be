package com.zapix.sync.zapix.integration.whatsapp;

public interface WhatsAppGateway {
    WhatsAppSendResult sendText(WhatsAppMessage message);
}

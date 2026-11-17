package com.zapix.sync.zapix.integration.whatsapp;

public record WhatsAppMessage(
        String instanceName,
        String phone,
        String text
) {
}

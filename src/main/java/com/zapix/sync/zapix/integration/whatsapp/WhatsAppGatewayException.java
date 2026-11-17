package com.zapix.sync.zapix.integration.whatsapp;

public class WhatsAppGatewayException extends RuntimeException {
    public WhatsAppGatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public WhatsAppGatewayException(String message) {
        super(message);
    }
}

package com.zapix.sync.zapix.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.zapix.sync.zapix.integration.whatsapp.WhatsAppGateway;
import com.zapix.sync.zapix.integration.whatsapp.WhatsAppMessage;
import com.zapix.sync.zapix.integration.whatsapp.WhatsAppSendResult;

import lombok.RequiredArgsConstructor;

// TEMPORARY: remove once the billing ruler uses the gateway
@RestController
@RequestMapping("/api/dev/whatsapp")
@RequiredArgsConstructor
public class WhatsAppTestController {
    private record TestRequest(String phone, String text) {}

    private final WhatsAppGateway whatsAppGateway;

    @Value("${evolution.instance:zapix-teste}")
    private String instance;

    @PostMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public WhatsAppSendResult test(@RequestBody TestRequest request) {
        return whatsAppGateway.sendText(new WhatsAppMessage(instance, request.phone(), request.text()));
    }
}

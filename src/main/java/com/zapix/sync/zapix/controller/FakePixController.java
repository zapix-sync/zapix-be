package com.zapix.sync.zapix.controller;

import java.time.Instant;

import com.zapix.sync.zapix.service.ChargePixService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.zapix.sync.zapix.service.ChargeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dev/pix")
@ConditionalOnProperty(name = "zapix.pix.gateway", havingValue = "fake", matchIfMissing = true)
@RequiredArgsConstructor
@Tag(name = "Dev (fake gateway)")
public class FakePixController {
    private final ChargeService chargeService;

    @PostMapping("/{gatewayChargeId}/pay")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Simulate a Pix payment (exists only with the fake gateway)")
    public void pay(@PathVariable String gatewayChargeId) {
        chargeService.confirmPayment(gatewayChargeId, Instant.now());
    }
}

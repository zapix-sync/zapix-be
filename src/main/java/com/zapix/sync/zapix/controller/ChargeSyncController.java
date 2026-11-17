package com.zapix.sync.zapix.controller;

import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.zapix.sync.zapix.dto.ChargeSyncResponse;
import com.zapix.sync.zapix.service.ChargeSyncService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies/{companyId}/charges")
@ConditionalOnProperty(name = "zapix.pix.gateway", havingValue = "stripe")
@RequiredArgsConstructor
@Tag(name = "Sync")
public class ChargeSyncController {

    private final ChargeSyncService chargeSyncService;

    @PostMapping("/{id}/sync")
    @ResponseStatus(HttpStatus.OK)
    public ChargeSyncResponse sync(@PathVariable UUID companyId, @PathVariable UUID id) {
        return chargeSyncService.sync(companyId, id);
    }
}

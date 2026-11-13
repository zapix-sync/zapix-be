package com.zapix.sync.zapix.controller;

import com.zapix.sync.zapix.dto.ChargeResponse;
import com.zapix.sync.zapix.enums.ChargeStatus;
import com.zapix.sync.zapix.service.ChargePixService;
import com.zapix.sync.zapix.service.ChargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies/{companyId}/charges")
@RequiredArgsConstructor
@Tag(name = "Charges")
public class ChargeController {
    private final ChargeService chargeService;
    private final ChargePixService chargePixService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Find a charge by id")
    public ChargeResponse findById(
            @PathVariable UUID companyId,
            @PathVariable UUID id) {
        return chargeService.findById(companyId, id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List charges of a company, optionally filtered by status")
    public PagedModel<ChargeResponse> findAll(
            @PathVariable UUID companyId,
            @RequestParam(required = false) ChargeStatus status,
            @ParameterObject Pageable pageable) {
        return new PagedModel<>(chargeService.findAll(companyId, status, pageable));
    }

    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Cancel a pending or overdue charge")
    public ChargeResponse cancel(@PathVariable UUID companyId, @PathVariable UUID id) {
        return chargeService.cancel(companyId, id);
    }

    @PostMapping("/{id}/pix")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Generate (or reuse) the Pix payment data for a charge")
    public ChargeResponse generate(@PathVariable UUID companyId, @PathVariable UUID id) {
        return chargePixService.generatePix(companyId, id);
    }
}

package com.zapix.sync.zapix.controller;

import com.zapix.sync.zapix.dto.SubscriptionCreateRequest;
import com.zapix.sync.zapix.dto.SubscriptionResponse;
import com.zapix.sync.zapix.dto.SubscriptionUpdateRequest;
import com.zapix.sync.zapix.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies/{companyId}/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a subscription")
    public SubscriptionResponse create(
            @PathVariable UUID companyId,
            @Valid
            @RequestBody SubscriptionCreateRequest request) {

        return subscriptionService.create(companyId, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Find a subscription by id")
    public SubscriptionResponse findById(
            @PathVariable UUID companyId,
            @PathVariable UUID id) {

        return subscriptionService.findById(companyId, id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List subscriptions of a company, optionally filtered by customer")
    public PagedModel<SubscriptionResponse> findAll(
            @PathVariable UUID companyId,
            @RequestParam(required = false) UUID customerId,
            @ParameterObject Pageable pageable) {

        return new PagedModel<>(subscriptionService.findAll(companyId, customerId, pageable));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update description, amount and billing cycle")
    public SubscriptionResponse update(
            @PathVariable UUID companyId,
            @PathVariable UUID id,
            @Valid @RequestBody SubscriptionUpdateRequest request) {

        return subscriptionService.update(companyId, id, request);
    }

    @PatchMapping("/{id}/pause")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Pause a subscription")
    public SubscriptionResponse pause(@PathVariable UUID companyId, @PathVariable UUID id) {
        return subscriptionService.pause(companyId, id);
    }

    @PatchMapping("/{id}/resume")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Resume a paused subscription")
    public SubscriptionResponse resume(@PathVariable UUID companyId, @PathVariable UUID id) {
        return subscriptionService.resume(companyId, id);
    }

    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Cancel a subscription")
    public SubscriptionResponse cancel(@PathVariable UUID companyId, @PathVariable UUID id) {
        return subscriptionService.cancel(companyId, id);
    }

}

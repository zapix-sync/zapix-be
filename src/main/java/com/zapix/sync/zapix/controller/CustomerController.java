package com.zapix.sync.zapix.controller;

import com.zapix.sync.zapix.dto.CustomerRequest;
import com.zapix.sync.zapix.dto.CustomerResponse;
import com.zapix.sync.zapix.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies/{companyId}/customers")
@RequiredArgsConstructor
@Tag(name = "Customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a customer")
    public CustomerResponse create(
            @PathVariable UUID companyId,
            @Valid
            @RequestBody CustomerRequest request) {
        return customerService.create(companyId, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Find a customer by id")
    public CustomerResponse findById(
            @PathVariable UUID companyId,
            @PathVariable UUID id) {
        return customerService.findById(companyId, id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List customers of a company, paginated")
    public PagedModel<CustomerResponse> findAll(
            @PathVariable UUID companyId,
            @ParameterObject Pageable pageable) {
        return new PagedModel<>(customerService.findAll(companyId, pageable));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a customer")
    public CustomerResponse update(
            @PathVariable UUID companyId,
            @PathVariable UUID id,
            @Valid
            @RequestBody CustomerRequest request) {
        return customerService.update(companyId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate a customer")
    public void deactivate(@PathVariable UUID companyId, @PathVariable UUID id) {
        customerService.deactivate(companyId, id);
    }
}

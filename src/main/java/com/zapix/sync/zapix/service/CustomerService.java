package com.zapix.sync.zapix.service;

import com.zapix.sync.zapix.dto.CustomerRequest;
import com.zapix.sync.zapix.dto.CustomerResponse;
import com.zapix.sync.zapix.exception.DuplicateResourceException;
import com.zapix.sync.zapix.exception.ResourceNotFoundException;
import com.zapix.sync.zapix.mapper.CustomerMapper;
import com.zapix.sync.zapix.model.Company;
import com.zapix.sync.zapix.model.Customer;
import com.zapix.sync.zapix.repository.CompanyRepository;
import com.zapix.sync.zapix.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerResponse create(UUID companyId, CustomerRequest request) {
        Company company = getCompanyOrThrow(companyId);

        if (request.document() != null
                && customerRepository.existsByCompanyIdAndDocument(companyId, request.document())) {
            throw duplicateDocument(request.document());
        }

        Customer customer = customerMapper.toEntity(request);

        customer.setCompany(company);

        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID companyId, UUID id) {
        return customerMapper.toResponse(getOrThrow(companyId, id));
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAll(UUID companyId, Pageable pageable) {
        getCompanyOrThrow(companyId);

        return customerRepository.findAllByCompanyId(companyId, pageable)
                .map(customerMapper::toResponse);
    }

    @Transactional
    public CustomerResponse update(UUID companyId, UUID id, CustomerRequest request) {
        Customer customer = getOrThrow(companyId, id);

        if (request.document() != null
                && customerRepository.existsByCompanyIdAndDocumentAndIdNot(companyId, request.document(), id)) {
            throw duplicateDocument(request.document());
        }
        customerMapper.updateEntity(request, customer);

        return customerMapper.toResponse(customerRepository.saveAndFlush(customer));
    }

    @Transactional
    public void deactivate(UUID companyId, UUID id) {
        Customer customer = getOrThrow(companyId, id);

        customer.setActive(false);
    }

    private Company getCompanyOrThrow(UUID companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));
    }

    private Customer getOrThrow(UUID companyId, UUID id) {
        return customerRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
    }

    private DuplicateResourceException duplicateDocument(String document) {
        return new DuplicateResourceException(
                "A customer with document " + document + " already exists in this company");
    }
}

package com.zapix.sync.zapix.service;

import com.zapix.sync.zapix.dto.SubscriptionCreateRequest;
import com.zapix.sync.zapix.dto.SubscriptionResponse;
import com.zapix.sync.zapix.dto.SubscriptionUpdateRequest;
import com.zapix.sync.zapix.enums.SubscriptionStatus;
import com.zapix.sync.zapix.exception.BusinessRuleException;
import com.zapix.sync.zapix.exception.ResourceNotFoundException;
import com.zapix.sync.zapix.mapper.SubscriptionMapper;
import com.zapix.sync.zapix.model.Company;
import com.zapix.sync.zapix.model.Customer;
import com.zapix.sync.zapix.model.Subscription;
import com.zapix.sync.zapix.repository.CompanyRepository;
import com.zapix.sync.zapix.repository.CustomerRepository;
import com.zapix.sync.zapix.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Transactional
    public SubscriptionResponse create(UUID companyId, SubscriptionCreateRequest request) {
        Company company = getCompanyOrThrow(companyId);

        Customer customer = customerRepository.findByIdAndCompanyId(request.customerId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.customerId()));

        if (!customer.isActive()) {
            throw new BusinessRuleException("Cannot create a subscription for an inactive customer");
        }

        Subscription subscription = subscriptionMapper.toEntity(request);
        subscription.setCompany(company);
        subscription.setCustomer(customer);

        return subscriptionMapper.toResponse(subscriptionRepository.save(subscription));
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse findById(UUID companyId, UUID id) {
        return subscriptionMapper.toResponse(getOrThrow(companyId, id));
    }

    @Transactional(readOnly = true)
    public Page<SubscriptionResponse> findAll(UUID companyId, UUID customerId, Pageable pageable) {
        getCompanyOrThrow(companyId);

        Page<Subscription> page = (customerId == null)
                ? subscriptionRepository.findAllByCompanyId(companyId, pageable)
                : subscriptionRepository.findAllByCompanyIdAndCustomerId(companyId, customerId, pageable);

        return page.map(subscriptionMapper::toResponse);
    }

    @Transactional
    public SubscriptionResponse update(UUID companyId, UUID id, SubscriptionUpdateRequest request) {
        Subscription subscription = getOrThrow(companyId, id);

        if (subscription.getStatus() == SubscriptionStatus.CANCELED) {
            throw new BusinessRuleException("A canceled subscription cannot be updated");
        }

        subscriptionMapper.updateEntity(request, subscription);

        return subscriptionMapper.toResponse(subscriptionRepository.saveAndFlush(subscription));
    }

    @Transactional
    public SubscriptionResponse pause(UUID companyId, UUID id) {
        Subscription subscription = getOrThrow(companyId, id);

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new BusinessRuleException("Only active subscriptions can be paused");
        }

        subscription.setStatus(SubscriptionStatus.PAUSED);

        return subscriptionMapper.toResponse(subscriptionRepository.saveAndFlush(subscription));
    }

    @Transactional
    public SubscriptionResponse resume(UUID companyId, UUID id) {
        Subscription subscription = getOrThrow(companyId, id);

        if (subscription.getStatus() != SubscriptionStatus.PAUSED) {
            throw new BusinessRuleException("Only paused subscriptions can be resumed");
        }

        subscription.setStatus(SubscriptionStatus.ACTIVE);

        return subscriptionMapper.toResponse(subscriptionRepository.saveAndFlush(subscription));
    }

    @Transactional
    public SubscriptionResponse cancel(UUID companyId, UUID id) {
        Subscription subscription = getOrThrow(companyId, id);

        if (subscription.getStatus() == SubscriptionStatus.CANCELED) {
            throw new BusinessRuleException("The subscription is already canceled");
        }

        subscription.setStatus(SubscriptionStatus.CANCELED);

        return subscriptionMapper.toResponse(subscriptionRepository.saveAndFlush(subscription));
    }

    private Company getCompanyOrThrow(UUID companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));
    }

    private Subscription getOrThrow(UUID companyId, UUID id) {
        return subscriptionRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + id));
    }
}

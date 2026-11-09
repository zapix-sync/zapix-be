package com.zapix.sync.zapix.repository;

import com.zapix.sync.zapix.model.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByIdAndCompanyId(UUID id, UUID companyId);
    Page<Subscription> findAllByCompanyId(UUID companyId, Pageable pageable);
    Page<Subscription> findAllByCompanyIdAndCustomerId(UUID companyId, UUID customerId, Pageable pageable);
}

package com.zapix.sync.zapix.repository;

import com.zapix.sync.zapix.enums.SubscriptionStatus;
import com.zapix.sync.zapix.model.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByIdAndCompanyId(UUID id, UUID companyId);
    Page<Subscription> findAllByCompanyId(UUID companyId, Pageable pageable);
    Page<Subscription> findAllByCompanyIdAndCustomerId(UUID companyId, UUID customerId, Pageable pageable);

    @Query("select s.id from Subscription s where s.status = :status and s.nextDueDate <= :limit")
    List<UUID> findDueIds(@Param("status") SubscriptionStatus status, @Param("limit") LocalDate limit);
}

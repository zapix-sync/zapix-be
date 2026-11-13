package com.zapix.sync.zapix.repository;

import com.zapix.sync.zapix.enums.ChargeStatus;
import com.zapix.sync.zapix.model.Charge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ChargeRepository extends JpaRepository<Charge, UUID> {
    Optional<Charge> findByIdAndCompanyId(UUID id, UUID companyId);
    Page<Charge> findAllByCompanyId(UUID companyId, Pageable pageable);
    Page<Charge> findAllByCompanyIdAndStatus(UUID companyId, ChargeStatus status, Pageable pageable);
    Optional<Charge> findByGatewayChargeId(String gatewayChargeId);

    boolean existsBySubscriptionIdAndDueDate(UUID subscriptionId, LocalDate dueDate);

    @Modifying
    @Query("""
            update Charge c
               set c.status = :overdue, c.updatedAt = :now
             where c.status = :pending and c.dueDate < :today
            """)
    int markOverdue(@Param("pending") ChargeStatus pending,
                    @Param("overdue") ChargeStatus overdue,
                    @Param("today") LocalDate today,
                    @Param("now")Instant now);
}

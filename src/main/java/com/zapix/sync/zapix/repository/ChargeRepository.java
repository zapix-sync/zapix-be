package com.zapix.sync.zapix.repository;

import com.zapix.sync.zapix.enums.ChargeStatus;
import com.zapix.sync.zapix.model.Charge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChargeRepository extends JpaRepository<Charge, UUID> {
    Optional<Charge> findByIdAndCompanyId(UUID id, UUID companyId);
    Page<Charge> findAllByCompanyId(UUID companyId, Pageable pageable);
    Page<Charge> findAllByCompanyIdAndStatus(UUID companyId, ChargeStatus status, Pageable pageable);
}

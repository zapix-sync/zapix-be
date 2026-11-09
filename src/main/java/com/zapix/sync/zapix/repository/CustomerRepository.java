package com.zapix.sync.zapix.repository;

import com.zapix.sync.zapix.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByIdAndCompanyId(UUID id, UUID companyId);
    Page<Customer> findAllByCompanyId(UUID companyId, Pageable pageable);
    boolean existsByCompanyIdAndDocument(UUID companyId, String document);
    boolean existsByCompanyIdAndDocumentAndIdNot(UUID companyId, String document, UUID id);
}

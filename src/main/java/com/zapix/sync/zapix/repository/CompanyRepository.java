package com.zapix.sync.zapix.repository;

import com.zapix.sync.zapix.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    boolean existsByDocument(String document);
    boolean existsByDocumentAndIdNot(String document, UUID id);
}

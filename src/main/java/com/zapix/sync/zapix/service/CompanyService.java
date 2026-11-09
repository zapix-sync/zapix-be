package com.zapix.sync.zapix.service;

import com.zapix.sync.zapix.dto.CompanyRequest;
import com.zapix.sync.zapix.dto.CompanyResponse;
import com.zapix.sync.zapix.exception.DuplicateResourceException;
import com.zapix.sync.zapix.exception.ResourceNotFoundException;
import com.zapix.sync.zapix.mapper.CompanyMapper;
import com.zapix.sync.zapix.model.Company;
import com.zapix.sync.zapix.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Transactional
    public CompanyResponse create(CompanyRequest request) {
        if (companyRepository.existsByDocument(request.document())) {
            throw new DuplicateResourceException(
                    "The company with document " + request.document() + "already exists!"
            );
        }

        Company company = companyMapper.toEntity(request);

        return companyMapper.toResponse(companyRepository.save(company));
    }

    @Transactional(readOnly = true)
    public CompanyResponse findById(UUID id) {
        return companyMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<CompanyResponse> findAll(Pageable pageable) {
        return companyRepository.findAll(pageable).map(companyMapper::toResponse);
    }

    @Transactional
    public CompanyResponse update(UUID id, CompanyRequest request) {
        Company company = getOrThrow(id);

        if (companyRepository.existsByDocumentAndIdNot(request.document(), id)) {
            throw new DuplicateResourceException(
                    "The company with document " + request.document() + "already exists!" + id
            );
        }

        companyMapper.updateEntity(request, company);

        return companyMapper.toResponse(companyRepository.saveAndFlush(company));
    }

    @Transactional
    public void deactivate(UUID id) {
        Company company = getOrThrow(id);

        company.setActive(false);
    }

    private Company getOrThrow(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + id));
    }
}

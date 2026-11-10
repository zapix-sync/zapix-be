package com.zapix.sync.zapix.service;

import com.zapix.sync.zapix.dto.ChargeResponse;
import com.zapix.sync.zapix.enums.ChargeStatus;
import com.zapix.sync.zapix.exception.BusinessRuleException;
import com.zapix.sync.zapix.exception.ResourceNotFoundException;
import com.zapix.sync.zapix.mapper.ChargeMapper;
import com.zapix.sync.zapix.model.Charge;
import com.zapix.sync.zapix.repository.ChargeRepository;
import com.zapix.sync.zapix.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChargeService {

    private final ChargeRepository chargeRepository;
    private final CompanyRepository companyRepository;
    private final ChargeMapper chargeMapper;

    @Transactional(readOnly = true)
    public ChargeResponse findById(UUID companyId, UUID id) {
        return chargeMapper.toResponse(getOrThrow(companyId, id));
    }

    @Transactional(readOnly = true)
    public Page<ChargeResponse> findAll(UUID companyId, ChargeStatus status, Pageable pageable) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found: " + companyId);
        }

        Page<Charge> page = (status == null)
                ? chargeRepository.findAllByCompanyId(companyId, pageable)
                : chargeRepository.findAllByCompanyIdAndStatus(companyId, status, pageable);

        return page.map(chargeMapper::toResponse);
    }

    @Transactional
    public ChargeResponse cancel(UUID companyId, UUID id) {
        Charge charge = getOrThrow(companyId, id);

        if (charge.getStatus() != ChargeStatus.PENDING && charge.getStatus() != ChargeStatus.OVERDUE) {
            throw new BusinessRuleException(
                    "Only pending or overdue charges can be canceled (current status: " + charge.getStatus() + ")");
        }

        charge.setStatus(ChargeStatus.CANCELED);

        return chargeMapper.toResponse(chargeRepository.saveAndFlush(charge));
    }

    private Charge getOrThrow(UUID companyId, UUID id) {
        return chargeRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Charge not found: " + id));
    }
}

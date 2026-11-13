package com.zapix.sync.zapix.model;

import com.zapix.sync.zapix.enums.ChargeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "charge")
public class Charge extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false, updatable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", updatable = false)
    private Subscription subscription;

    private String description;

    private BigDecimal amount;

    private String gatewayChargeId;

    private String pixCopyPaste;

    private String pixQrCodeUrl;

    private Instant pixExpiresAt;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private ChargeStatus status = ChargeStatus.PENDING;

    private Instant paidAt;
}

package com.zapix.sync.zapix.model;

import java.time.Instant;
import java.util.UUID;

import com.zapix.sync.zapix.enums.NotificationStatus;
import com.zapix.sync.zapix.enums.NotificationType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "charge_notification")
public class ChargeNotification extends BaseEntity {

    private UUID companyId;

    private UUID chargeId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private int attempts;

    private String gatewayMessageId;

    private String errorMessage;

    private Instant sentAt;
}
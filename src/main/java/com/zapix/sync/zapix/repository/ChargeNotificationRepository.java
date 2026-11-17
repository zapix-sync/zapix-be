package com.zapix.sync.zapix.repository;

import com.zapix.sync.zapix.enums.NotificationType;
import com.zapix.sync.zapix.model.ChargeNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChargeNotificationRepository extends JpaRepository<ChargeNotification, UUID> {
    Optional<ChargeNotification> findByChargeIdAndType(UUID chargeId, NotificationType type);
}

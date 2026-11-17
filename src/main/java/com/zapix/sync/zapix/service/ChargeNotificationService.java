package com.zapix.sync.zapix.service;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zapix.sync.zapix.enums.ChargeStatus;
import com.zapix.sync.zapix.enums.NotificationStatus;
import com.zapix.sync.zapix.enums.NotificationType;
import com.zapix.sync.zapix.integration.whatsapp.WhatsAppGateway;
import com.zapix.sync.zapix.integration.whatsapp.WhatsAppMessage;
import com.zapix.sync.zapix.integration.whatsapp.WhatsAppSendResult;
import com.zapix.sync.zapix.model.Charge;
import com.zapix.sync.zapix.model.ChargeNotification;
import com.zapix.sync.zapix.repository.ChargeNotificationRepository;
import com.zapix.sync.zapix.repository.ChargeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargeNotificationService {

    private static final int MAX_ATTEMPTS = 3;
    private static final List<ChargeStatus> OPEN_STATUSES = List.of(ChargeStatus.PENDING, ChargeStatus.OVERDUE);
    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ChargeRepository chargeRepository;
    private final ChargeNotificationRepository notificationRepository;
    private final ChargePixService chargePixService;
    private final WhatsAppGateway whatsAppGateway;

    @Value("${evolution.instance:Zapix}")
    private String instance;

    @Value("${zapix.notification.delay-ms:3000}")
    private long delayMs;

    public int runRuler() {
        LocalDate today = LocalDate.now(ZONE);
        int sent = 0;

        for (NotificationType type : NotificationType.values()) {
            LocalDate dueDate = today.plusDays(type.getDaysUntilDue());
            List<Charge> charges = chargeRepository.findNotifiable(dueDate, OPEN_STATUSES);

            for (Charge charge : charges) {
                try {
                    if (notifyCharge(charge, type)) {
                        sent++;
                        pause();
                    }
                } catch (Exception e) {
                    log.error("Failed to process notification {} for charge {}", type, charge.getId(), e);
                }
            }
        }

        return sent;
    }

    private boolean notifyCharge(Charge charge, NotificationType type) {
        ChargeNotification record = notificationRepository
                .findByChargeIdAndType(charge.getId(), type)
                .orElse(null);

        if (record != null
                && (record.getStatus() == NotificationStatus.SENT || record.getAttempts() >= MAX_ATTEMPTS)) {
            return false;
        }

        if (record == null) {
            record = new ChargeNotification();
            record.setCompanyId(charge.getCompany().getId());
            record.setChargeId(charge.getId());
            record.setType(type);
        }
        record.setAttempts(record.getAttempts() + 1);

        try {
            String paymentUrl = chargePixService
                    .generatePix(charge.getCompany().getId(), charge.getId())
                    .paymentUrl();

            String text = buildMessage(charge, type, paymentUrl);
            WhatsAppSendResult result = whatsAppGateway.sendText(
                    new WhatsAppMessage(instance, charge.getCustomer().getPhone(), text));

            record.setStatus(NotificationStatus.SENT);
            record.setGatewayMessageId(result.messageId());
            record.setSentAt(Instant.now());
            record.setErrorMessage(null);
            notificationRepository.save(record);

            log.info("Notification {} sent for charge {}", type, charge.getId());
            return true;
        } catch (Exception e) {
            record.setStatus(NotificationStatus.FAILED);
            record.setErrorMessage(truncate(e.getMessage()));
            notificationRepository.save(record);

            log.error("Notification {} failed for charge {} (attempt {})",
                    type, charge.getId(), record.getAttempts(), e);
            return false;
        }
    }

    private String buildMessage(Charge charge, NotificationType type, String paymentUrl) {
        String name = firstName(charge.getCustomer().getName());
        String what = charge.getDescription();
        String amount = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR")).format(charge.getAmount());
        String due = charge.getDueDate().format(DATE_FORMAT);

        String intro = switch (type) {
            case REMINDER_D3 -> "Olá, %s! Passando para lembrar que a cobrança \"%s\" de %s vence em %s."
                    .formatted(name, what, amount, due);
            case DUE_TODAY -> "Olá, %s! A cobrança \"%s\" de %s vence hoje."
                    .formatted(name, what, amount);
            case OVERDUE_D1 -> "Olá, %s! A cobrança \"%s\" de %s venceu ontem (%s)."
                    .formatted(name, what, amount, due);
            case OVERDUE_D5 -> "Olá, %s! A cobrança \"%s\" de %s está em atraso desde %s."
                    .formatted(name, what, amount, due);
        };

        return intro + "\n\nPague pelo Pix: " + paymentUrl
                + "\n\nSe você já pagou, desconsidere esta mensagem.";
    }

    private String firstName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "cliente";
        }
        return fullName.trim().split("\\s+")[0];
    }

    private String truncate(String message) {
        if (message == null) {
            return null;
        }
        return message.length() > 500 ? message.substring(0, 500) : message;
    }

    // Spaces out the messages so the number does not send a burst
    private void pause() {
        if (delayMs <= 0) {
            return;
        }
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
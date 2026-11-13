package com.zapix.sync.zapix.scheduler;

import com.zapix.sync.zapix.enums.SubscriptionStatus;
import com.zapix.sync.zapix.repository.SubscriptionRepository;
import com.zapix.sync.zapix.service.ChargeGenerator;
import com.zapix.sync.zapix.service.ChargeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingScheduler {
    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    private final SubscriptionRepository subscriptionRepository;
    private final ChargeService chargeService;
    private final ChargeGenerator chargeGenerator;

    @Value("${zapix.billing.days-ahead:3}")
    private int daysAhead;

    @Scheduled(cron = "${zapix.billing.cron:0 0 6 * * *}", zone = "America/Sao_Paulo")
    public void run() {
        var today = LocalDate.now(ZONE);
        var limit = today.plusDays(daysAhead);

        int overdue = chargeService.markOverdue(today);
        List<UUID> subscriptionIds = subscriptionRepository.findDueIds(SubscriptionStatus.ACTIVE, limit);

        var outcomes = subscriptionIds.stream()
                .map(id -> generateSafely(id, limit))
                .toList();

        int created = outcomes.stream().mapToInt(Outcome::created).sum();
        long failed = outcomes.stream().filter(Outcome::failed).count();

        log.info("Billing job finished: {} charges marked overdue, {} subscriptions checked, "
                + "{} charges created, {} failures", overdue, subscriptionIds.size(), created, failed);
    }

    private Outcome generateSafely(UUID subscriptionId, LocalDate limit) {
        try {
            return new Outcome(chargeGenerator.generateFor(subscriptionId, limit), false);
        } catch (Exception ex) {
            log.error("Failed to generate charges for subscription {}", subscriptionId, ex);
            return new Outcome(0, true);
        }
    }

    private record Outcome(int created, boolean failed) {
    }
}

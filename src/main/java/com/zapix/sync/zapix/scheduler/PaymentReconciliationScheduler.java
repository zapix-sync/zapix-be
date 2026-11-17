package com.zapix.sync.zapix.scheduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zapix.sync.zapix.service.ChargeSyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "zapix.pix.gateway", havingValue = "stripe")
@RequiredArgsConstructor
public class PaymentReconciliationScheduler {

    private final ChargeSyncService chargeSyncService;

    @Scheduled(cron = "${zapix.reconciliation.cron:0 */5 * * * *}", zone = "America/Sao_Paulo")
    public void run() {
        int recovered = chargeSyncService.reconcilePending();

        if (recovered > 0) {
            log.warn("Reconciliation recovered {} payment(s) that the webhook did not report", recovered);
        }
    }
}

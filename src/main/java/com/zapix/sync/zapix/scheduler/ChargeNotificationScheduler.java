package com.zapix.sync.zapix.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zapix.sync.zapix.service.ChargeNotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChargeNotificationScheduler {

    private final ChargeNotificationService notificationService;

    @Scheduled(cron = "${zapix.notification.cron:0 0 9,14 * * *}", zone = "America/Sao_Paulo")
    public void run() {
        int sent = notificationService.runRuler();
        log.info("Billing ruler finished: {} message(s) sent", sent);
    }
}
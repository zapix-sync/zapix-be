package com.zapix.sync.zapix.enums;

import java.time.LocalDate;
import java.time.YearMonth;

public enum BillingCycle {
    WEEKLY,
    MONTHLY,
    YEARLY;

    public LocalDate nextAfter(LocalDate date, int billingDay) {
        return switch (this) {
            case WEEKLY -> date.plusWeeks(1);
            case MONTHLY -> withBillingDay(YearMonth.from(date).plusMonths(1), billingDay);
            case YEARLY -> withBillingDay(YearMonth.from(date).plusYears(1), billingDay);
        };
    }

    private static LocalDate withBillingDay(YearMonth month, int billingDay) {
        return month.atDay(Math.min(billingDay, month.lengthOfMonth()));
    }
}

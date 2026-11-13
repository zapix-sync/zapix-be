package com.zapix.sync.zapix.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import com.zapix.sync.zapix.enums.BillingCycle;
import org.junit.jupiter.api.Test;

class BillingCycleTest {

    @Test
    void monthlyKeepsBillingDayAfterShortMonth() {
        LocalDate february = BillingCycle.MONTHLY.nextAfter(LocalDate.of(2027, 1, 31), 31);
        LocalDate march = BillingCycle.MONTHLY.nextAfter(february, 31);
        LocalDate april = BillingCycle.MONTHLY.nextAfter(march, 31);

        assertEquals(LocalDate.of(2027, 2, 28), february);
        assertEquals(LocalDate.of(2027, 3, 31), march);
        assertEquals(LocalDate.of(2027, 4, 30), april);
    }

    @Test
    void monthlyRollsOverToNextYear() {
        LocalDate next = BillingCycle.MONTHLY.nextAfter(LocalDate.of(2026, 12, 10), 10);

        assertEquals(LocalDate.of(2027, 1, 10), next);
    }

    @Test
    void weeklyAddsSevenDays() {
        LocalDate next = BillingCycle.WEEKLY.nextAfter(LocalDate.of(2026, 11, 11), 11);

        assertEquals(LocalDate.of(2026, 11, 18), next);
    }

    @Test
    void yearlyKeepsFebruary29OnLeapYears() {
        LocalDate y2029 = BillingCycle.YEARLY.nextAfter(LocalDate.of(2028, 2, 29), 29);
        LocalDate y2030 = BillingCycle.YEARLY.nextAfter(y2029, 29);
        LocalDate y2031 = BillingCycle.YEARLY.nextAfter(y2030, 29);
        LocalDate y2032 = BillingCycle.YEARLY.nextAfter(y2031, 29);

        assertEquals(LocalDate.of(2029, 2, 28), y2029);
        assertEquals(LocalDate.of(2030, 2, 28), y2030);
        assertEquals(LocalDate.of(2031, 2, 28), y2031);
        assertEquals(LocalDate.of(2032, 2, 29), y2032);
    }
}
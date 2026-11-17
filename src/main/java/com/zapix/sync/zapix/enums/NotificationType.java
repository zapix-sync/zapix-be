package com.zapix.sync.zapix.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    REMINDER_D3(3),
    DUE_TODAY(0),
    OVERDUE_D1(-1),
    OVERDUE_D5(-5);

    private final int daysUnitDue;
}

package com.zapix.sync.zapix.service;

import com.zapix.sync.zapix.enums.SubscriptionStatus;
import com.zapix.sync.zapix.exception.ResourceNotFoundException;
import com.zapix.sync.zapix.model.Charge;
import com.zapix.sync.zapix.model.Subscription;
import com.zapix.sync.zapix.repository.ChargeRepository;
import com.zapix.sync.zapix.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ChargeGenerator {

    private final SubscriptionRepository subscriptionRepository;
    private final ChargeRepository chargeRepository;

    @Transactional
    public int generateFor(UUID subscriptionId, LocalDate limit) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + subscriptionId));

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE || !subscription.getCustomer().isActive()) {
            return 0;
        }

        var billingCycle = subscription.getBillingCycle();
        var billingDay = subscription.getBillingDay();

        var dueDates = Stream.iterate(
                subscription.getNextDueDate(),
                dueDate -> !dueDate.isAfter(limit),
                dueDate -> billingCycle.nextAfter(dueDate, billingDay)
        ).toList();

        int created = 0;
        for (LocalDate dueDate : dueDates) {
            if (!chargeRepository.existsBySubscriptionIdAndDueDate(subscriptionId, dueDate)) {
                chargeRepository.save(buildCharge(subscription, dueDate));
                created++;
            }
        }

        if (!dueDates.isEmpty()) {
            subscription.setNextDueDate(billingCycle.nextAfter(dueDates.getLast(), billingDay));
        }

        return created;
    }

    private Charge buildCharge(Subscription subscription, LocalDate dueDate) {
        Charge charge = new Charge();
        charge.setCompany(subscription.getCompany());
        charge.setCustomer(subscription.getCustomer());
        charge.setSubscription(subscription);
        charge.setDescription(subscription.getDescription());
        charge.setAmount(subscription.getAmount());
        charge.setDueDate(dueDate);
        return charge;
    }
}

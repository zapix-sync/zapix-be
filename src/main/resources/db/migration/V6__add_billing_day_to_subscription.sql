ALTER TABLE subscription ADD COLUMN billing_day INTEGER;

UPDATE subscription SET billing_day = EXTRACT(DAY FROM next_due_date);

ALTER TABLE subscription ALTER COLUMN billing_day SET NOT NULL;

ALTER TABLE subscription
    ADD CONSTRAINT ck_subscription_billing_day CHECK (billing_day BETWEEN 1 AND 31);
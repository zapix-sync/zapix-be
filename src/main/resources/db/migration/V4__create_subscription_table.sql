CREATE TABLE subscription (
                              id             UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
                              company_id     UUID           NOT NULL REFERENCES company (id),
                              customer_id    UUID           NOT NULL REFERENCES customer (id),
                              description    VARCHAR(150)   NOT NULL,
                              amount         NUMERIC(12, 2) NOT NULL,
                              billing_cycle  VARCHAR(10)    NOT NULL,
                              next_due_date  DATE           NOT NULL,
                              status         VARCHAR(10)    NOT NULL DEFAULT 'ACTIVE',
                              created_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
                              updated_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
                              CONSTRAINT ck_subscription_amount CHECK (amount > 0),
                              CONSTRAINT ck_subscription_billing_cycle CHECK (billing_cycle IN ('WEEKLY', 'MONTHLY', 'YEARLY')),
                              CONSTRAINT ck_subscription_status CHECK (status IN ('ACTIVE', 'PAUSED', 'CANCELED'))
);

CREATE INDEX idx_subscription_company_id ON subscription (company_id);
CREATE INDEX idx_subscription_customer_id ON subscription (customer_id);
CREATE INDEX idx_subscription_status_next_due_date ON subscription (status, next_due_date);
CREATE TABLE charge (
                        id               UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
                        company_id       UUID           NOT NULL REFERENCES company (id),
                        customer_id      UUID           NOT NULL REFERENCES customer (id),
                        subscription_id  UUID           REFERENCES subscription (id),
                        description      VARCHAR(150)   NOT NULL,
                        amount           NUMERIC(12, 2) NOT NULL,
                        due_date         DATE           NOT NULL,
                        status           VARCHAR(10)    NOT NULL DEFAULT 'PENDING',
                        paid_at          TIMESTAMPTZ,
                        created_at       TIMESTAMPTZ    NOT NULL DEFAULT now(),
                        updated_at       TIMESTAMPTZ    NOT NULL DEFAULT now(),
                        CONSTRAINT ck_charge_amount CHECK (amount > 0),
                        CONSTRAINT ck_charge_status CHECK (status IN ('PENDING', 'PAID', 'OVERDUE', 'CANCELED')),
                        CONSTRAINT uk_charge_subscription_due_date UNIQUE (subscription_id, due_date)
);

CREATE INDEX idx_charge_company_id ON charge (company_id);
CREATE INDEX idx_charge_customer_id ON charge (customer_id);
CREATE INDEX idx_charge_status_due_date ON charge (status, due_date);
CREATE TABLE charge_notification (
    id                 UUID PRIMARY KEY,
    company_id         UUID         NOT NULL,
    charge_id          UUID         NOT NULL REFERENCES charge (id),
    type               VARCHAR(20)  NOT NULL,
    status             VARCHAR(20)  NOT NULL,
    attempts           INTEGER      NOT NULL DEFAULT 0,
    gateway_message_id VARCHAR(100),
    error_message      TEXT,
    sent_at            TIMESTAMPTZ,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uk_charge_notification_charge_type UNIQUE (charge_id, type)
);

CREATE INDEX idx_charge_notification_company ON charge_notification (company_id);
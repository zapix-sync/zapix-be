ALTER TABLE charge
    ADD COLUMN gateway_charge_id VARCHAR(100),
    ADD COLUMN pix_copy_paste    TEXT,
    ADD COLUMN pix_qr_code_url   TEXT,
    ADD COLUMN pix_expires_at    TIMESTAMPTZ;

CREATE UNIQUE INDEX uk_charge_gateway_charge_id
    ON charge (gateway_charge_id)
    WHERE gateway_charge_id IS NOT NULL;
CREATE TABLE customer (
                          id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                          company_id        UUID         NOT NULL REFERENCES company (id),
                          name              VARCHAR(150) NOT NULL,
                          document          VARCHAR(14),
                          email             VARCHAR(150),
                          phone             VARCHAR(20)  NOT NULL,
                          whatsapp_opt_in   BOOLEAN      NOT NULL DEFAULT FALSE,
                          active            BOOLEAN      NOT NULL DEFAULT TRUE,
                          created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
                          updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
                          CONSTRAINT uk_customer_company_document UNIQUE (company_id, document)
);

CREATE INDEX idx_customer_company_id ON customer (company_id);
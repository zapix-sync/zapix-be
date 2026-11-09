CREATE TABLE company (
                         id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                         name        VARCHAR(150) NOT NULL,
                         document    VARCHAR(14)  NOT NULL UNIQUE,
                         email       VARCHAR(150) NOT NULL,
                         phone       VARCHAR(20),
                         active      BOOLEAN      NOT NULL DEFAULT TRUE,
                         created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                         updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS companies (
    company_id   UUID         NOT NULL,
    name         VARCHAR(255) NOT NULL,
    email_domain VARCHAR(255) NOT NULL,
    phone        VARCHAR(30)  NOT NULL,
    PRIMARY KEY (company_id)
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS company_id UUID;
ALTER TABLE users ADD CONSTRAINT fk_users_company FOREIGN KEY (company_id) REFERENCES companies (company_id);

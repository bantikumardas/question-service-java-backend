CREATE TABLE companies (
    company_id   CHAR(36)      NOT NULL,
    name         VARCHAR2(255) NOT NULL,
    email_domain VARCHAR2(255) NOT NULL,
    phone        VARCHAR2(30)  NOT NULL,
    PRIMARY KEY (company_id)
);

ALTER TABLE users ADD company_id CHAR(36);
ALTER TABLE users ADD CONSTRAINT fk_users_company FOREIGN KEY (company_id) REFERENCES companies (company_id);

ALTER TABLE test ADD COLUMN IF NOT EXISTS company_id UUID;
ALTER TABLE test ADD CONSTRAINT fk_test_company FOREIGN KEY (company_id) REFERENCES companies (company_id);

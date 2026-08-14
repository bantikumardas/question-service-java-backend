ALTER TABLE test ADD company_id CHAR(36);
ALTER TABLE test ADD CONSTRAINT fk_test_company FOREIGN KEY (company_id) REFERENCES companies (company_id);

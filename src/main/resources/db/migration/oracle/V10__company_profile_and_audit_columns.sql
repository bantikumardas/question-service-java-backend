ALTER TABLE companies ADD created_by_user_id CHAR(36);
ALTER TABLE companies ADD CONSTRAINT fk_companies_created_by FOREIGN KEY (created_by_user_id) REFERENCES users (user_id);

ALTER TABLE companies ADD logo_url VARCHAR2(255);
ALTER TABLE companies ADD email VARCHAR2(255);
ALTER TABLE companies ADD CONSTRAINT uq_companies_email UNIQUE (email);

ALTER TABLE companies ADD max_active_tests NUMBER(10, 0) DEFAULT 1 NOT NULL;
ALTER TABLE companies ADD active BOOLEAN DEFAULT TRUE NOT NULL;
ALTER TABLE companies ADD subscription_active BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE companies ADD subscription_expiry_date TIMESTAMP;
ALTER TABLE companies ADD subscription_start_date TIMESTAMP;
ALTER TABLE companies ADD created_time TIMESTAMP DEFAULT SYSDATE NOT NULL;
ALTER TABLE companies ADD updated_time TIMESTAMP DEFAULT SYSDATE NOT NULL;

ALTER TABLE test_invite ADD invitation_sent_time TIMESTAMP DEFAULT SYSDATE NOT NULL;
ALTER TABLE test_invite ADD updated_time TIMESTAMP DEFAULT SYSDATE NOT NULL;

ALTER TABLE users ADD updated_time TIMESTAMP DEFAULT SYSDATE NOT NULL;

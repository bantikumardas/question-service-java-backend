CREATE TABLE test_invite (
    invite_id          CHAR(36)      NOT NULL,
    test_test_id       CHAR(36)      NOT NULL,
    invited_by_user_id CHAR(36)      NOT NULL,
    email              VARCHAR2(150) NOT NULL,
    invitation_code    VARCHAR2(64),
    status             VARCHAR2(20)  DEFAULT 'PENDING' NOT NULL,
    failure_reason     VARCHAR2(500),
    attempt_count      NUMBER(10, 0) DEFAULT 0 NOT NULL,
    PRIMARY KEY (invite_id),
    UNIQUE (test_test_id, email),
    FOREIGN KEY (test_test_id) REFERENCES test (test_id),
    FOREIGN KEY (invited_by_user_id) REFERENCES users (user_id)
);

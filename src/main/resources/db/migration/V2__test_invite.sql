CREATE TABLE IF NOT EXISTS test_invite (
    invite_id          UUID         NOT NULL,
    test_test_id       UUID         NOT NULL,
    invited_by_user_id UUID         NOT NULL,
    email              VARCHAR(150) NOT NULL,
    invitation_code    VARCHAR(64),
    status             VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    failure_reason     VARCHAR(500),
    attempt_count      INTEGER      NOT NULL DEFAULT 0,
    PRIMARY KEY (invite_id),
    UNIQUE (test_test_id, email),
    FOREIGN KEY (test_test_id) REFERENCES test (test_id),
    FOREIGN KEY (invited_by_user_id) REFERENCES users (user_id)
);

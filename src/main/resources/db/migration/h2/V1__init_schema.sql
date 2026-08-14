CREATE TABLE IF NOT EXISTS users (
    user_id       UUID         NOT NULL,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    created_time  TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS test (
    test_id            UUID         NOT NULL,
    test_name          VARCHAR(255) NOT NULL,
    total_time_seconds BIGINT       NOT NULL,
    created_by         UUID         NOT NULL,
    created_time       TIMESTAMP,
    status             VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    PRIMARY KEY (test_id)
);

CREATE TABLE IF NOT EXISTS coding_question (
    coding_question_id UUID         NOT NULL,
    test_id            UUID         NOT NULL,
    title              VARCHAR(200) NOT NULL,
    description        CLOB         NOT NULL,
    image_url_1        VARCHAR(500),
    image_url_2        VARCHAR(500),
    difficulty         VARCHAR(10)  NOT NULL DEFAULT 'MEDIUM',
    marks              INTEGER      NOT NULL DEFAULT 10,
    order_index        INTEGER      NOT NULL DEFAULT 0,
    PRIMARY KEY (coding_question_id),
    FOREIGN KEY (test_id) REFERENCES test (test_id)
);

CREATE TABLE IF NOT EXISTS coding_question_paragraphs (
    coding_question_id UUID    NOT NULL,
    paragraph          CLOB,
    paragraph_index    INTEGER NOT NULL,
    PRIMARY KEY (coding_question_id, paragraph_index),
    FOREIGN KEY (coding_question_id) REFERENCES coding_question (coding_question_id)
);

CREATE TABLE IF NOT EXISTS coding_question_constraints (
    coding_question_id UUID    NOT NULL,
    constraint_text     CLOB,
    constraint_index   INTEGER NOT NULL,
    PRIMARY KEY (coding_question_id, constraint_index),
    FOREIGN KEY (coding_question_id) REFERENCES coding_question (coding_question_id)
);

CREATE TABLE IF NOT EXISTS test_case (
    test_case_id       UUID          NOT NULL,
    coding_question_id UUID          NOT NULL,
    input              VARCHAR(1000) NOT NULL,
    expected_output    VARCHAR(1000) NOT NULL,
    explanation        CLOB,
    is_hidden          BOOLEAN       NOT NULL DEFAULT FALSE,
    is_example         BOOLEAN       NOT NULL DEFAULT FALSE,
    PRIMARY KEY (test_case_id),
    FOREIGN KEY (coding_question_id) REFERENCES coding_question (coding_question_id)
);

CREATE TABLE IF NOT EXISTS test_question (
    question_id       UUID        NOT NULL,
    test_id           UUID        NOT NULL,
    question          CLOB        NOT NULL,
    question_image_url VARCHAR(500),
    option_a          CLOB        NOT NULL,
    option_b          CLOB        NOT NULL,
    option_c          CLOB        NOT NULL,
    option_d          CLOB        NOT NULL,
    correct_option    VARCHAR(1)  NOT NULL,
    marks             INTEGER     NOT NULL DEFAULT 1,
    order_index       INTEGER     NOT NULL DEFAULT 0,
    difficulty_level  VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
    PRIMARY KEY (question_id),
    FOREIGN KEY (test_id) REFERENCES test (test_id)
);

CREATE TABLE IF NOT EXISTS exam_session (
    session_id UUID        NOT NULL,
    test_id    UUID        NOT NULL,
    user_id    UUID        NOT NULL,
    start_time TIMESTAMP,
    end_time   TIMESTAMP   NOT NULL,
    status     VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    PRIMARY KEY (session_id),
    FOREIGN KEY (test_id) REFERENCES test (test_id)
);

CREATE TABLE users (
    user_id       CHAR(36)      NOT NULL,
    name          VARCHAR2(100) NOT NULL,
    email         VARCHAR2(150) NOT NULL,
    password_hash VARCHAR2(255) NOT NULL,
    role          VARCHAR2(20)  NOT NULL,
    created_time  TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE (email)
);

CREATE TABLE test (
    test_id            CHAR(36)      NOT NULL,
    test_name          VARCHAR2(255) NOT NULL,
    total_time_seconds NUMBER(19, 0) NOT NULL,
    created_by         CHAR(36)      NOT NULL,
    created_time       TIMESTAMP,
    status             VARCHAR2(20)  DEFAULT 'DRAFT' NOT NULL,
    PRIMARY KEY (test_id)
);

CREATE TABLE coding_question (
    coding_question_id CHAR(36)      NOT NULL,
    test_id            CHAR(36)      NOT NULL,
    title               VARCHAR2(200) NOT NULL,
    description        CLOB          NOT NULL,
    image_url_1         VARCHAR2(500),
    image_url_2         VARCHAR2(500),
    difficulty          VARCHAR2(10)  DEFAULT 'MEDIUM' NOT NULL,
    marks               NUMBER(10, 0) DEFAULT 10 NOT NULL,
    order_index         NUMBER(10, 0) DEFAULT 0 NOT NULL,
    PRIMARY KEY (coding_question_id),
    FOREIGN KEY (test_id) REFERENCES test (test_id)
);

CREATE TABLE coding_question_paragraphs (
    coding_question_id CHAR(36)     NOT NULL,
    paragraph          CLOB,
    paragraph_index    NUMBER(10, 0) NOT NULL,
    PRIMARY KEY (coding_question_id, paragraph_index),
    FOREIGN KEY (coding_question_id) REFERENCES coding_question (coding_question_id)
);

CREATE TABLE coding_question_constraints (
    coding_question_id CHAR(36)     NOT NULL,
    constraint_text     CLOB,
    constraint_index   NUMBER(10, 0) NOT NULL,
    PRIMARY KEY (coding_question_id, constraint_index),
    FOREIGN KEY (coding_question_id) REFERENCES coding_question (coding_question_id)
);

CREATE TABLE test_case (
    test_case_id       CHAR(36)       NOT NULL,
    coding_question_id CHAR(36)       NOT NULL,
    input              VARCHAR2(1000) NOT NULL,
    expected_output    VARCHAR2(1000) NOT NULL,
    explanation        CLOB,
    is_hidden          BOOLEAN DEFAULT FALSE NOT NULL,
    is_example         BOOLEAN DEFAULT FALSE NOT NULL,
    PRIMARY KEY (test_case_id),
    FOREIGN KEY (coding_question_id) REFERENCES coding_question (coding_question_id)
);

CREATE TABLE test_question (
    question_id        CHAR(36)     NOT NULL,
    test_id             CHAR(36)     NOT NULL,
    question            CLOB         NOT NULL,
    question_image_url  VARCHAR2(500),
    option_a             CLOB         NOT NULL,
    option_b             CLOB         NOT NULL,
    option_c             CLOB         NOT NULL,
    option_d             CLOB         NOT NULL,
    correct_option       VARCHAR2(1)  NOT NULL,
    marks                NUMBER(10, 0) DEFAULT 1 NOT NULL,
    order_index          NUMBER(10, 0) DEFAULT 0 NOT NULL,
    difficulty_level     VARCHAR2(10) DEFAULT 'MEDIUM' NOT NULL,
    PRIMARY KEY (question_id),
    FOREIGN KEY (test_id) REFERENCES test (test_id)
);

CREATE TABLE exam_session (
    session_id CHAR(36)     NOT NULL,
    test_id    CHAR(36)     NOT NULL,
    user_id    CHAR(36)     NOT NULL,
    start_time TIMESTAMP,
    end_time   TIMESTAMP    NOT NULL,
    status     VARCHAR2(20) DEFAULT 'ACTIVE' NOT NULL,
    PRIMARY KEY (session_id),
    FOREIGN KEY (test_id) REFERENCES test (test_id)
);

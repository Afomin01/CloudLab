CREATE TABLE user_table
(
    id          UUID PRIMARY KEY,
    username    VARCHAR(255) UNIQUE NOT NULL,
    password    TEXT                NOT NULL,
    telegram_id TEXT,
    role        varchar(64)         NOT NULL,
    blocked     BOOLEAN,
    quota       BIGINT
);

CREATE TABLE generation_task
(
        id UUID PRIMARY KEY,
        user_id UUID NOT NULL REFERENCES user_table,
        creation_time TIMESTAMP NOT NULL,
        parameters TEXT,
        status VARCHAR(64) NOT NULL
);
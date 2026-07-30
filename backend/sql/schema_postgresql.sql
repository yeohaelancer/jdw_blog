CREATE TABLE sample (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200)    NOT NULL,
    content     TEXT,
    created_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

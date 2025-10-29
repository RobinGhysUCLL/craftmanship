-- Drop tables if they exist (including dependencies)
DROP TABLE IF EXISTS "user" CASCADE;

-- USER Table Creation
CREATE TABLE "user" (
    id BIGSERIAL NOT NULL,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL,

    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(3) NOT NULL,

    CONSTRAINT user_pkey PRIMARY KEY (id),
    CONSTRAINT user_username_unique UNIQUE (username)
);

CREATE UNIQUE INDEX user_username_key ON "user" (username);

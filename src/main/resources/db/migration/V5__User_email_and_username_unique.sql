-- Drop tables if they exist (including dependencies)
DROP TABLE IF EXISTS "user" CASCADE;

-- USER Table Creation
CREATE TABLE "user"
(
    id        UUID NOT NULL,
    user_name TEXT NOT NULL,
    password  TEXT NOT NULL,
    email     TEXT NOT NULL,
    CONSTRAINT user_pkey PRIMARY KEY (id),
    CONSTRAINT user_user_name_unique UNIQUE (user_name),
    CONSTRAINT user_email_unique UNIQUE (email)
);

CREATE UNIQUE INDEX user_user_name_key ON "user" (user_name);
CREATE UNIQUE INDEX user_email_key ON "user" (email);
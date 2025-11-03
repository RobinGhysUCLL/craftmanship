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

-- MESSAGE Table Creation
CREATE TABLE messages (
                          id UUID NOT NULL,
                          user_id UUID NOT NULL,
                          user_message TEXT NOT NULL,
                          ai_response TEXT NOT NULL,
                          "timestamp" TIMESTAMP NOT NULL,
                          CONSTRAINT chat_pkey PRIMARY KEY (id),
                          CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES "user"(id)
);

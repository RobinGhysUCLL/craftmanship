-- JOURNAL table creation
CREATE TABLE journal (
    id UUID NOT NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    mood TEXT,
    tags TEXT,
    "date" DATE,
    user_id UUID NOT NULL,
    CONSTRAINT journal_pkey PRIMARY KEY (id),
    CONSTRAINT fk_journal_user FOREIGN KEY (user_id) REFERENCES "user"(id)
);
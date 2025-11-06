DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS chat CASCADE;

-- Maak chat tabel
CREATE TABLE chat (
  id UUID NOT NULL,
  user_id UUID NOT NULL UNIQUE,
  CONSTRAINT chat_pkey PRIMARY KEY (id),
  CONSTRAINT fk_chat_user FOREIGN KEY (user_id) REFERENCES "user"(id)
);

-- Maak messages tabel (met verwijzing naar chat)
CREATE TABLE messages (
  id UUID NOT NULL,
  user_id UUID NOT NULL,
  message TEXT NOT NULL,
  ai BOOLEAN NOT NULL,
  "timestamp" TIMESTAMP NOT NULL,
  chat_id UUID,
  CONSTRAINT messages_pkey PRIMARY KEY (id),
  CONSTRAINT fk_messages_user FOREIGN KEY (user_id) REFERENCES "user"(id),
  CONSTRAINT fk_messages_chat FOREIGN KEY (chat_id) REFERENCES chat(id)
);

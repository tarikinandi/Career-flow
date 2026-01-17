ALTER TABLE applications ADD COLUMN user_id BIGINT;
ALTER TABLE decks ADD COLUMN user_id BIGINT;

ALTER TABLE applications ADD CONSTRAINT fk_application_user FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE decks ADD CONSTRAINT fk_deck_user FOREIGN KEY (user_id) REFERENCES users(id);

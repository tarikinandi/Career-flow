CREATE TABLE applications (
                              id BIGSERIAL PRIMARY KEY,
                              company_name VARCHAR(255) NOT NULL,
                              position VARCHAR(255) NOT NULL,
                              status VARCHAR(50) NOT NULL,
                              location VARCHAR(255),
                              salary_range VARCHAR(255),
                              applied_date DATE,
                              notes TEXT,
                              created_at TIMESTAMP,
                              updated_at TIMESTAMP
);

CREATE TABLE interviews (
                            id BIGSERIAL PRIMARY KEY,
                            application_id BIGINT NOT NULL REFERENCES applications(id),
                            round VARCHAR(50),
                            interview_date TIMESTAMP,
                            interviewer_name VARCHAR(255),
                            meeting_link VARCHAR(255),
                            notes TEXT,
                            follow_up_sent BOOLEAN DEFAULT FALSE,
                            created_at TIMESTAMP,
                            updated_at TIMESTAMP
);

CREATE TABLE decks (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description VARCHAR(255),
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP
);

CREATE TABLE flashcards (
                            id BIGSERIAL PRIMARY KEY,
                            deck_id BIGINT NOT NULL REFERENCES decks(id),
                            front_content TEXT NOT NULL,
                            back_content TEXT NOT NULL,
                            easiness_factor DOUBLE PRECISION DEFAULT 2.5,
                            repetitions INTEGER DEFAULT 0,
                            interval_days INTEGER DEFAULT 0,
                            next_review_date TIMESTAMP,
                            created_at TIMESTAMP,
                            updated_at TIMESTAMP
);
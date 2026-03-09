CREATE TABLE punch_events (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    punch_type VARCHAR(50) NOT NULL,
    punch_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_punch_events_user_id ON punch_events(user_id);
CREATE INDEX idx_punch_events_punch_at ON punch_events(punch_at);
CREATE INDEX idx_punch_events_user_date ON punch_events(user_id, punch_at);

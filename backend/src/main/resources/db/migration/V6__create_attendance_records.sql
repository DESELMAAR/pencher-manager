CREATE TABLE attendance_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    attendance_date DATE NOT NULL,
    attendance_status VARCHAR(50) NOT NULL,
    planned_start_time TIME,
    actual_start_time TIME,
    delay_minutes INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE(user_id, attendance_date)
);

CREATE INDEX idx_attendance_user_date ON attendance_records(user_id, attendance_date);
CREATE INDEX idx_attendance_date ON attendance_records(attendance_date);
CREATE INDEX idx_attendance_status ON attendance_records(attendance_status);

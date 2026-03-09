CREATE TABLE planning_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_date DATE NOT NULL,
    planned_start_time TIME,
    planned_end_time TIME,
    scheduled BOOLEAN,
    shift_type VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE(user_id, plan_date)
);

CREATE INDEX idx_planning_user_date ON planning_records(user_id, plan_date);
CREATE INDEX idx_planning_date ON planning_records(plan_date);

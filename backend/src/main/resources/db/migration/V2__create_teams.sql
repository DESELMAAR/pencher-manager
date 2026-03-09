CREATE TABLE teams (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    department_id BIGINT NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
    leader_user_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_teams_department_id ON teams(department_id);
CREATE INDEX idx_teams_leader_user_id ON teams(leader_user_id);

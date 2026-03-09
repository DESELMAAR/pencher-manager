CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    employee_id VARCHAR(100) UNIQUE,
    phone_number VARCHAR(50),
    hiring_date DATE,
    role VARCHAR(50) NOT NULL,
    department_id BIGINT REFERENCES departments(id) ON DELETE SET NULL,
    team_id BIGINT REFERENCES teams(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX idx_users_email ON users(email);
CREATE UNIQUE INDEX idx_users_employee_id ON users(employee_id);
CREATE INDEX idx_users_department_id ON users(department_id);
CREATE INDEX idx_users_team_id ON users(team_id);


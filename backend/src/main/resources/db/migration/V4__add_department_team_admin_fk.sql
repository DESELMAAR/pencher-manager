ALTER TABLE departments ADD CONSTRAINT fk_departments_admin_user
    FOREIGN KEY (admin_user_id) REFERENCES users(id) ON DELETE SET NULL;
ALTER TABLE teams ADD CONSTRAINT fk_teams_leader_user
    FOREIGN KEY (leader_user_id) REFERENCES users(id) ON DELETE SET NULL;

-- Seed fictitious users for testing API endpoints
-- Run this in psql when connected to pencherdb (see docs/SEED-USERS.md)
-- All seed users have password: password (BCrypt hash below)

-- BCrypt hash for "password" (use this to login: email + password "password")
-- Skip if seed already exists
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'superadmin@pencher.com') THEN

    -- 1. Department
    INSERT INTO departments (name, description, admin_user_id, created_at, updated_at)
    VALUES ('IT', 'Information Technology', NULL, NOW(), NOW());

    -- 2. Team (depends on department id 1)
    INSERT INTO teams (name, description, department_id, leader_user_id, created_at, updated_at)
    VALUES ('Development', 'Dev team', 1, NULL, NOW(), NOW());

    -- 3. Users (same BCrypt hash for "password" for all)
    INSERT INTO users (full_name, email, password, status, employee_id, phone_number, hiring_date, role, department_id, team_id, created_at, updated_at)
    VALUES
      ('Super Admin', 'superadmin@pencher.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE', 'SA001', NULL, NULL, 'SUPER_ADMIN', NULL, NULL, NOW(), NOW()),
      ('Department Admin', 'deptadmin@pencher.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE', 'DA001', NULL, NULL, 'DEPARTMENT_ADMIN', 1, NULL, NOW(), NOW()),
      ('Team Leader', 'teamleader@pencher.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE', 'TL001', NULL, NULL, 'TEAM_LEADER', 1, 1, NOW(), NOW()),
      ('John Employee', 'employee@pencher.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE', 'EMP001', '+1234567890', (CURRENT_DATE - 365), 'EMPLOYEE', 1, 1, NOW(), NOW());

    -- 4. Set department admin and team leader
    UPDATE departments SET admin_user_id = (SELECT id FROM users WHERE email = 'deptadmin@pencher.com') WHERE id = 1;
    UPDATE teams SET leader_user_id = (SELECT id FROM users WHERE email = 'teamleader@pencher.com') WHERE id = 1;

    RAISE NOTICE 'Seed users created. Login with any email above and password: password';
  ELSE
    RAISE NOTICE 'Seed users already exist. Skipping.';
  END IF;
END $$;

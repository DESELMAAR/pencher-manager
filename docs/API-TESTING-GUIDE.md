# Pencher Manager – API Testing Guide (Step by Step)

This guide lists **all API endpoints** and how to test them in order. Use **Postman**, **curl**, or any REST client. Base URL: **http://localhost:8080** (ensure the backend is running).

---

## Table of contents

1. [Prerequisites](#1-prerequisites)
2. [Step 1: Auth (no token)](#2-step-1-auth-no-token)
3. [Step 2: Auth (with token)](#3-step-2-auth-with-token)
4. [Step 3: Departments](#4-step-3-departments)
5. [Step 4: Teams](#5-step-4-teams)
6. [Step 5: Users](#6-step-5-users)
7. [Step 6: Punches](#7-step-6-punches)
8. [Step 7: Attendance](#8-step-7-attendance)
9. [Step 8: Planning](#9-step-8-planning)
10. [Step 9: Dashboard](#10-step-9-dashboard)
11. [Quick reference – all endpoints](#11-quick-reference--all-endpoints)

---

## 1. Prerequisites

- Backend running: `mvn spring-boot:run` in the `backend` folder.
- PostgreSQL running (e.g. `docker-compose -f docker-compose-pencher.yml up -d`).
- For **protected** endpoints, send the JWT in the header:
  ```http
  Authorization: Bearer <your-access-token>
  ```
- Content type for JSON bodies:
  ```http
  Content-Type: application/json
  ```

**Seed users (if you ran with profile `dev` or seeded data):**

| Role            | Email                    | Password        |
|-----------------|--------------------------|-----------------|
| Super Admin     | superadmin@pencher.com   | SuperAdmin123!  |
| Department Admin| deptadmin@pencher.com    | DeptAdmin123!   |
| Team Leader     | teamleader@pencher.com   | TeamLeader123!  |
| Employee        | employee@pencher.com    | Employee123!    |

---

## 2. Step 1: Auth (no token)

These endpoints **do not** require `Authorization`.

### 2.1 Login

Get an access token (and refresh token). Use this token for all other requests.

| Field    | Value |
|----------|--------|
| **Method** | `POST` |
| **URL**    | `http://localhost:8080/api/auth/login` |
| **Headers** | `Content-Type: application/json` |
| **Body**    | JSON (see below) |

**Request body:**
```json
{
  "email": "superadmin@pencher.com",
  "password": "SuperAdmin123!"
}
```

**Example response (200):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 900,
  "tokenType": "Bearer"
}
```

**Copy `accessToken`** and use it as: `Authorization: Bearer <accessToken>` in the next steps.

---

### 2.2 Register

Create a new user (no auth required).

| Field    | Value |
|----------|--------|
| **Method** | `POST` |
| **URL**    | `http://localhost:8080/api/auth/register` |
| **Headers** | `Content-Type: application/json` |
| **Body**    | JSON (see below) |

**Request body:**
```json
{
  "fullName": "New User",
  "email": "newuser@test.com",
  "password": "Password123!",
  "role": "EMPLOYEE",
  "employeeId": "EMP999",
  "phoneNumber": "+1234567890",
  "hiringDate": "2025-01-15",
  "departmentId": 1,
  "teamId": 1
}
```

**Example response (200):** User object (id, fullName, email, role, departmentId, teamId, etc.).

---

### 2.3 Refresh token

Get a new access token using the refresh token.

| Field    | Value |
|----------|--------|
| **Method** | `POST` |
| **URL**    | `http://localhost:8080/api/auth/refresh` |
| **Headers** | `Content-Type: application/json` |
| **Body**    | JSON (see below) |

**Request body:**
```json
{
  "refreshToken": "<paste-refresh-token-from-login-response>"
}
```

**Example response (200):** Same shape as login (`accessToken`, `refreshToken`, `expiresIn`, `tokenType`).

---

## 3. Step 2: Auth (with token)

Use the token from **Login** in the header: `Authorization: Bearer <accessToken>`.

### 3.1 Get current user (Me)

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/auth/me` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (200):** User object (id, fullName, email, status, role, departmentId, teamId, etc.).

---

### 3.2 Logout

| Field    | Value |
|----------|--------|
| **Method** | `POST` |
| **URL**    | `http://localhost:8080/api/auth/logout` |
| **Headers** | `Authorization: Bearer <accessToken>` |
| **Body**    | None |

**Example response (204):** No content. Client should discard the token.

---

## 4. Step 3: Departments

All department endpoints require auth. **Super Admin** can do full CRUD; **Department Admin** sees only their department.

### 4.1 Create department

| Field    | Value |
|----------|--------|
| **Method** | `POST` |
| **URL**    | `http://localhost:8080/api/departments` |
| **Headers** | `Authorization: Bearer <accessToken>`, `Content-Type: application/json` |
| **Body**    | JSON (see below) |

**Request body:**
```json
{
  "name": "Engineering",
  "description": "Software development"
}
```

**Example response (201):** Department object (id, name, description, adminUserId, createdAt, updatedAt).

---

### 4.2 List all departments

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/departments` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (200):** Array of departments.

---

### 4.3 Get department by ID

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/departments/{id}` |
| **Example** | `GET http://localhost:8080/api/departments/1` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (200):** Single department object.

---

### 4.4 Update department

| Field    | Value |
|----------|--------|
| **Method** | `PUT` |
| **URL**    | `http://localhost:8080/api/departments/{id}` |
| **Example** | `PUT http://localhost:8080/api/departments/1` |
| **Headers** | `Authorization: Bearer <accessToken>`, `Content-Type: application/json` |
| **Body**    | JSON (see below) |

**Request body:**
```json
{
  "name": "Engineering Updated",
  "description": "Updated description"
}
```

**Example response (200):** Updated department object.

---

### 4.5 Get teams by department

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/departments/{departmentId}/teams` |
| **Example** | `GET http://localhost:8080/api/departments/1/teams` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (200):** Array of teams in that department.

---

### 4.6 Delete department

| Field    | Value |
|----------|--------|
| **Method** | `DELETE` |
| **URL**    | `http://localhost:8080/api/departments/{id}` |
| **Example** | `DELETE http://localhost:8080/api/departments/2` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (204):** No content.

---

## 5. Step 4: Teams

Use token. **Super Admin** or **Department Admin** can create/update/delete teams in their scope.

### 5.1 Create team

| Field    | Value |
|----------|--------|
| **Method** | `POST` |
| **URL**    | `http://localhost:8080/api/teams` |
| **Headers** | `Authorization: Bearer <accessToken>`, `Content-Type: application/json` |
| **Body**    | JSON (see below) |

**Request body:**
```json
{
  "departmentId": 1,
  "name": "Backend Team",
  "description": "Backend developers"
}
```

**Example response (201):** Team object (id, name, description, departmentId, leaderUserId, etc.).

---

### 5.2 List all teams

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/teams` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (200):** Array of teams.

---

### 5.3 Get team by ID

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/teams/{id}` |
| **Example** | `GET http://localhost:8080/api/teams/1` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (200):** Single team object.

---

### 5.4 Update team

| Field    | Value |
|----------|--------|
| **Method** | `PUT` |
| **URL**    | `http://localhost:8080/api/teams/{id}` |
| **Example** | `PUT http://localhost:8080/api/teams/1` |
| **Headers** | `Authorization: Bearer <accessToken>`, `Content-Type: application/json` |
| **Body**    | JSON (see below) |

**Request body:**
```json
{
  "departmentId": 1,
  "name": "Backend Team Updated",
  "description": "Updated description"
}
```

**Example response (200):** Updated team object.

---

### 5.5 Get employees by team

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/teams/{teamId}/employees` |
| **Example** | `GET http://localhost:8080/api/teams/1/employees` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (200):** Array of users (employees) in that team.

---

### 5.6 Delete team

| Field    | Value |
|----------|--------|
| **Method** | `DELETE` |
| **URL**    | `http://localhost:8080/api/teams/{id}` |
| **Example** | `DELETE http://localhost:8080/api/teams/2` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (204):** No content.

---

## 6. Step 5: Users

Use token. Access depends on role (Super Admin sees all; Department Admin / Team Leader see their scope).

### 6.1 Create user

| Field    | Value |
|----------|--------|
| **Method** | `POST` |
| **URL**    | `http://localhost:8080/api/users` |
| **Headers** | `Authorization: Bearer <accessToken>`, `Content-Type: application/json` |
| **Body**    | JSON (see below) |

**Request body:**
```json
{
  "fullName": "Jane Doe",
  "email": "jane@test.com",
  "password": "Password123!",
  "employeeId": "EMP002",
  "phoneNumber": "+1987654321",
  "hiringDate": "2025-02-01",
  "role": "EMPLOYEE",
  "departmentId": 1,
  "teamId": 1
}
```

**Example response (201):** User object (without password).

---

### 6.2 List all users

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/users` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (200):** Array of users.

---

### 6.3 Get user by ID

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/users/{id}` |
| **Example** | `GET http://localhost:8080/api/users/1` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (200):** Single user object.

---

### 6.4 Update user

| Field    | Value |
|----------|--------|
| **Method** | `PUT` |
| **URL**    | `http://localhost:8080/api/users/{id}` |
| **Example** | `PUT http://localhost:8080/api/users/1` |
| **Headers** | `Authorization: Bearer <accessToken>`, `Content-Type: application/json` |
| **Body**    | JSON (see below) |

**Request body (all fields optional):**
```json
{
  "fullName": "Jane Doe Updated",
  "email": "jane.updated@test.com",
  "password": "NewPassword123!",
  "employeeId": "EMP002",
  "phoneNumber": "+1987654321",
  "hiringDate": "2025-02-01",
  "role": "EMPLOYEE",
  "departmentId": 1,
  "teamId": 1
}
```

**Example response (200):** Updated user object.

---

### 6.5 Update user status (patch)

| Field    | Value |
|----------|--------|
| **Method** | `PATCH` |
| **URL**    | `http://localhost:8080/api/users/{id}/status?status=ACTIVE` |
| **Example** | `PATCH http://localhost:8080/api/users/1/status?status=INACTIVE` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query param:** `status` = one of: `ACTIVE`, `INACTIVE`, `SUSPENDED`.

**Example response (200):** User object with updated status.

---

### 6.6 Delete user

| Field    | Value |
|----------|--------|
| **Method** | `DELETE` |
| **URL**    | `http://localhost:8080/api/users/{id}` |
| **Example** | `DELETE http://localhost:8080/api/users/5` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (204):** No content.

---

## 7. Step 6: Punches

Use token. Employees punch; managers can view team/department punches. **Order:** Start Work → (optional breaks/lunch) → End Shift.

### 7.1 Punch: Start Work

| Field    | Value |
|----------|--------|
| **Method** | `POST` |
| **URL**    | `http://localhost:8080/api/punches/start-work` |
| **Headers** | `Authorization: Bearer <accessToken>` |
| **Body**    | None |

**Example response (201):** Punch event object (id, userId, punchType: "START_WORK", punchAt, createdAt).

---

### 7.2 Punch: Break 1

| Field    | Value |
|----------|--------|
| **Method** | `POST` |
| **URL**    | `http://localhost:8080/api/punches/break-1` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (201):** Punch event (punchType: "BREAK_1"). Requires START_WORK same day first.

---

### 7.3 Punch: Break 2

| Field    | Value |
|----------|--------|
| **Method** | `POST` |
| **URL**    | `http://localhost:8080/api/punches/break-2` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (201):** Punch event (punchType: "BREAK_2").

---

### 7.4 Punch: Lunch Break

| Field    | Value |
|----------|--------|
| **Method** | `POST` |
| **URL**    | `http://localhost:8080/api/punches/lunch-break` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (201):** Punch event (punchType: "LUNCH_BREAK").

---

### 7.5 Punch: End Shift

| Field    | Value |
|----------|--------|
| **Method** | `POST` |
| **URL**    | `http://localhost:8080/api/punches/end-shift` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (201):** Punch event (punchType: "END_SHIFT"). Requires START_WORK same day first.

---

### 7.6 My punches today

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/punches/me/today` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (200):** Array of punch events for the current user for today.

---

### 7.7 My punch history

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/punches/me/history?page=0&size=20` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query params:** `page` (default 0), `size` (default 20).

**Example response (200):** Array of punch events (most recent first).

---

### 7.8 Punches by employee

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/punches/employee/{employeeId}?from=2025-01-01&to=2025-12-31` |
| **Example** | `GET http://localhost:8080/api/punches/employee/4?from=2025-03-01&to=2025-03-08` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query params:** `from`, `to` (dates in ISO format: YYYY-MM-DD).

**Example response (200):** Array of punch events for that employee in the date range.

---

### 7.9 Punches by team (for a date)

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/punches/team/{teamId}?date=2025-03-08` |
| **Example** | `GET http://localhost:8080/api/punches/team/1?date=2025-03-08` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query param:** `date` (YYYY-MM-DD).

**Example response (200):** Array of punch events for all members of that team on that date.

---

### 7.10 Punches by department (for a date)

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/punches/department/{departmentId}?date=2025-03-08` |
| **Example** | `GET http://localhost:8080/api/punches/department/1?date=2025-03-08` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query param:** `date` (YYYY-MM-DD).

**Example response (200):** Array of punch events for the department on that date.

---

## 8. Step 7: Attendance

Use token. Attendance is derived from punches and planning (ON_TIME, LATE, ABSENT).

### 8.1 My attendance today

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/attendance/me/today` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (200):** Attendance record (userId, attendanceDate, attendanceStatus, plannedStartTime, actualStartTime, delayMinutes). May be ABSENT if no START_WORK today.

---

### 8.2 My attendance history

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/attendance/me/history?page=0&size=20` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query params:** `page`, `size`.

**Example response (200):** Array of attendance records.

---

### 8.3 Daily attendance (all for a date)

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/attendance/daily?date=2025-03-08` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query param:** `date` (YYYY-MM-DD). Requires role that can view attendance (admin/team leader).

**Example response (200):** Array of attendance records for that date.

---

### 8.4 Late attendance (for a date)

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/attendance/late?date=2025-03-08` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query param:** `date` (YYYY-MM-DD).

**Example response (200):** Array of attendance records with status LATE.

---

### 8.5 Absent (for a date)

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/attendance/absent?date=2025-03-08` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query param:** `date` (YYYY-MM-DD).

**Example response (200):** Array of attendance records with status ABSENT.

---

### 8.6 On-time attendance (for a date)

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/attendance/on-time?date=2025-03-08` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query param:** `date` (YYYY-MM-DD).

**Example response (200):** Array of attendance records with status ON_TIME.

---

### 8.7 Attendance summary

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/attendance/summary?from=2025-01-01&to=2025-12-31` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query params:** `from`, `to` (YYYY-MM-DD).

**Example response (200):** Object with `from`, `to`, `onTime`, `late`, `absent` counts.

---

### 8.8 Attendance by employee

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/attendance/employee/{employeeId}?from=2025-03-01&to=2025-03-08` |
| **Example** | `GET http://localhost:8080/api/attendance/employee/4?from=2025-03-01&to=2025-03-08` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query params:** `from`, `to` (YYYY-MM-DD).

**Example response (200):** Array of attendance records for that employee.

---

### 8.9 Attendance by team (for a date)

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/attendance/team/{teamId}?date=2025-03-08` |
| **Example** | `GET http://localhost:8080/api/attendance/team/1?date=2025-03-08` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query param:** `date` (YYYY-MM-DD).

**Example response (200):** Array of attendance records for that team.

---

### 8.10 Attendance by department (for a date)

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/attendance/department/{departmentId}?date=2025-03-08` |
| **Example** | `GET http://localhost:8080/api/attendance/department/1?date=2025-03-08` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query param:** `date` (YYYY-MM-DD).

**Example response (200):** Array of attendance records for that department.

---

## 9. Step 8: Planning

Use token. Planning comes from external API or mock (scheduled start/end per employee per date).

### 9.1 Get planning for employee

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/planning/employee/{employeeId}?from=2025-03-01&to=2025-03-31` |
| **Example** | `GET http://localhost:8080/api/planning/employee/4?from=2025-03-01&to=2025-03-31` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Query params:** `from`, `to` (YYYY-MM-DD).

**Example response (200):** Array of planning records (planDate, plannedStartTime, plannedEndTime, scheduled, shiftType).

---

### 9.2 Sync planning for one employee

| Field    | Value |
|----------|--------|
| **Method** | `POST` |
| **URL**    | `http://localhost:8080/api/planning/sync/{employeeId}` |
| **Example** | `POST http://localhost:8080/api/planning/sync/4` |
| **Headers** | `Authorization: Bearer <accessToken>` |
| **Body**    | None |

**Example response (204):** No content. Fetches from Planning API (or mock) and stores for that user.

---

### 9.3 Sync planning for all

| Field    | Value |
|----------|--------|
| **Method** | `POST` |
| **URL**    | `http://localhost:8080/api/planning/sync-all` |
| **Headers** | `Authorization: Bearer <accessToken>` (Super Admin only) |
| **Body**    | None |

**Example response (204):** No content. Syncs planning for all users.

---

## 10. Step 9: Dashboard

Use token. Each dashboard is for a role; access is enforced by backend.

### 10.1 Super Admin dashboard

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/dashboard/super-admin` |
| **Headers** | `Authorization: Bearer <accessToken>` (Super Admin only) |

**Example response (200):** Object with totalDepartments, totalTeams, totalEmployees, onTimeToday, lateToday, absentToday, byStatus.

---

### 10.2 Department Admin dashboard

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/dashboard/department-admin` |
| **Headers** | `Authorization: Bearer <accessToken>` (Department Admin) |

**Example response (200):** Same shape, scoped to the admin’s department.

---

### 10.3 Team Leader dashboard

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/dashboard/team-leader` |
| **Headers** | `Authorization: Bearer <accessToken>` (Team Leader) |

**Example response (200):** Same shape, scoped to the leader’s team.

---

### 10.4 Employee dashboard

| Field    | Value |
|----------|--------|
| **Method** | `GET` |
| **URL**    | `http://localhost:8080/api/dashboard/employee` |
| **Headers** | `Authorization: Bearer <accessToken>` |

**Example response (200):** Same shape, scoped to the current user (e.g. onTimeToday/lateToday/absentToday 0 or 1).

---

## 11. Quick reference – all endpoints

| # | Method | Endpoint | Auth |
|---|--------|----------|------|
| **Auth** |
| 1 | POST | `/api/auth/login` | No |
| 2 | POST | `/api/auth/register` | No |
| 3 | POST | `/api/auth/refresh` | No |
| 4 | GET | `/api/auth/me` | Yes |
| 5 | POST | `/api/auth/logout` | Yes |
| **Departments** |
| 6 | POST | `/api/departments` | Yes |
| 7 | GET | `/api/departments` | Yes |
| 8 | GET | `/api/departments/{id}` | Yes |
| 9 | PUT | `/api/departments/{id}` | Yes |
| 10 | DELETE | `/api/departments/{id}` | Yes |
| 11 | GET | `/api/departments/{departmentId}/teams` | Yes |
| **Teams** |
| 12 | POST | `/api/teams` | Yes |
| 13 | GET | `/api/teams` | Yes |
| 14 | GET | `/api/teams/{id}` | Yes |
| 15 | PUT | `/api/teams/{id}` | Yes |
| 16 | DELETE | `/api/teams/{id}` | Yes |
| 17 | GET | `/api/teams/{teamId}/employees` | Yes |
| **Users** |
| 18 | POST | `/api/users` | Yes |
| 19 | GET | `/api/users` | Yes |
| 20 | GET | `/api/users/{id}` | Yes |
| 21 | PUT | `/api/users/{id}` | Yes |
| 22 | PATCH | `/api/users/{id}/status?status=ACTIVE\|INACTIVE\|SUSPENDED` | Yes |
| 23 | DELETE | `/api/users/{id}` | Yes |
| **Punches** |
| 24 | POST | `/api/punches/start-work` | Yes |
| 25 | POST | `/api/punches/break-1` | Yes |
| 26 | POST | `/api/punches/break-2` | Yes |
| 27 | POST | `/api/punches/lunch-break` | Yes |
| 28 | POST | `/api/punches/end-shift` | Yes |
| 29 | GET | `/api/punches/me/today` | Yes |
| 30 | GET | `/api/punches/me/history?page=0&size=20` | Yes |
| 31 | GET | `/api/punches/employee/{employeeId}?from=YYYY-MM-DD&to=YYYY-MM-DD` | Yes |
| 32 | GET | `/api/punches/team/{teamId}?date=YYYY-MM-DD` | Yes |
| 33 | GET | `/api/punches/department/{departmentId}?date=YYYY-MM-DD` | Yes |
| **Attendance** |
| 34 | GET | `/api/attendance/me/today` | Yes |
| 35 | GET | `/api/attendance/me/history?page=0&size=20` | Yes |
| 36 | GET | `/api/attendance/daily?date=YYYY-MM-DD` | Yes |
| 37 | GET | `/api/attendance/late?date=YYYY-MM-DD` | Yes |
| 38 | GET | `/api/attendance/absent?date=YYYY-MM-DD` | Yes |
| 39 | GET | `/api/attendance/on-time?date=YYYY-MM-DD` | Yes |
| 40 | GET | `/api/attendance/summary?from=YYYY-MM-DD&to=YYYY-MM-DD` | Yes |
| 41 | GET | `/api/attendance/employee/{employeeId}?from=YYYY-MM-DD&to=YYYY-MM-DD` | Yes |
| 42 | GET | `/api/attendance/team/{teamId}?date=YYYY-MM-DD` | Yes |
| 43 | GET | `/api/attendance/department/{departmentId}?date=YYYY-MM-DD` | Yes |
| **Planning** |
| 44 | GET | `/api/planning/employee/{employeeId}?from=YYYY-MM-DD&to=YYYY-MM-DD` | Yes |
| 45 | POST | `/api/planning/sync/{employeeId}` | Yes |
| 46 | POST | `/api/planning/sync-all` | Yes (Super Admin) |
| **Dashboard** |
| 47 | GET | `/api/dashboard/super-admin` | Yes (Super Admin) |
| 48 | GET | `/api/dashboard/department-admin` | Yes (Dept Admin) |
| 49 | GET | `/api/dashboard/team-leader` | Yes (Team Leader) |
| 50 | GET | `/api/dashboard/employee` | Yes |

**Total: 50 endpoints.**

---

## Suggested testing order (one full flow)

1. **POST** `/api/auth/login` with Super Admin → save `accessToken`.
2. **GET** `/api/auth/me` → check current user.
3. **GET** `/api/departments` → list (may be empty or seeded).
4. **POST** `/api/departments` → create one → note `id`.
5. **GET** `/api/departments/{id}` and **GET** `/api/departments/{id}/teams`.
6. **POST** `/api/teams` with `departmentId` → note team `id`.
7. **GET** `/api/teams` and **GET** `/api/teams/{teamId}/employees`.
8. **POST** `/api/users` (EMPLOYEE, same department/team).
9. **GET** `/api/users` and **GET** `/api/users/{id}`.
10. Login as that employee (or use token if you created with known password), then **POST** `/api/punches/start-work`.
11. **GET** `/api/punches/me/today` and **GET** `/api/attendance/me/today`.
12. **POST** `/api/punches/end-shift`.
13. **GET** `/api/attendance/summary?from=2025-03-01&to=2025-03-31`.
14. **GET** `/api/dashboard/super-admin` (as Super Admin again).
15. **GET** `/api/planning/employee/{employeeId}?from=...&to=...` and **POST** `/api/planning/sync/{employeeId}`.

For **Swagger UI** (interactive docs and “Try it out”): open **http://localhost:8080/swagger-ui.html** in the browser (auth via **Authorize** with `Bearer <accessToken>`).

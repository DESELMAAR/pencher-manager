# Pencher Manager

Workforce attendance and hierarchy management (PFE project).

## Stack

| Layer      | Technology |
|-----------|------------|
| Backend   | Spring Boot 3.4.x, Maven, Java 17, PostgreSQL |
| Frontend  | Next.js 15 (App Router) |
| Auth      | JWT, BCrypt, role-based access |
| API docs  | Swagger / OpenAPI |
| DB        | Flyway migrations, PostgreSQL |
| Tests     | JUnit 5, Mockito, Testcontainers |

**Note:** The project uses Spring Boot 3.4.x for broad JDK/Lombok compatibility. For Spring Boot 4.0.3, upgrade the `backend/pom.xml` parent version and ensure Lombok supports your JDK.

## Structure

```
projectspringbootlearning/
├── backend/                 # Spring Boot API
│   ├── src/main/java/...   # controller, service, repository, entity, dto, mapper, security, config, exception, integration, scheduler
│   └── src/main/resources/db/migration/  # Flyway
├── frontend/                # Next.js App Router
│   └── src/app/             # login, dashboard, departments, teams, employees, punch, attendance
├── postman/                 # Postman collection + environment
├── docker-compose-pencher.yml  # PostgreSQL for Pencher
└── PENCHER-README.md        # This file
```

## Quick start

### 1. Database

```bash
docker-compose -f docker-compose-pencheryml up -d
```

### 2. Backend

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

- API: http://localhost:8080  
- Swagger: http://localhost:8080/swagger-ui.html  
- Seed user: `superadmin@pencher.com` / `SuperAdmin123!`

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

- App: http://localhost:3000  
- Set `NEXT_PUBLIC_API_URL=http://localhost:8080` if needed (default is already 8080).

### 4. Postman

1. Import `postman/Pencher-Manager-API.postman_collection.json`.
2. Import `postman/Pencher-Manager.postman_environment.json`.
3. Select environment **Pencher Manager**, run **Auth → Login** (saves token).
4. Call other endpoints (token is sent automatically).

## Roles and hierarchy

- **SUPER_ADMIN** – creates departments, full access.
- **DEPARTMENT_ADMIN** – manages teams and users in own department.
- **TEAM_LEADER** – sees own team’s punches/attendance.
- **EMPLOYEE** – punches (Start Work, Breaks, Lunch, End Shift), sees own attendance.

## Punch flow

1. Employee punches **Start Work** (and optionally breaks / lunch / end shift).
2. Attendance is computed: planned start (from Planning API or mock) vs actual START_WORK.
3. Status: **ON_TIME** (within grace period), **LATE**, or **ABSENT** (no start punch).

## Configuration

- **Backend:** `backend/src/main/resources/application.properties`  
  - JWT: `pencher.jwt.secret`, `pencher.jwt.access-expiration-ms`  
  - Grace period: `pencher.attendance.grace-period-minutes`  
  - Planning: `pencher.planning.use-mock`, `pencher.planning.api.url`
- **Frontend:** `NEXT_PUBLIC_API_URL` (default `http://localhost:8080`).

## Tests

```bash
cd backend
mvn test
```

- Unit: attendance status logic, JWT.
- Integration: auth with Testcontainers PostgreSQL.

## Deliverables checklist

- [x] Backend layered structure (controller, service, repository, entity, dto, mapper, security, config, exception, integration, scheduler)
- [x] Entities: User, Department, Team, AttendanceRecord, PunchEvent, PlanningRecord, AuditLog
- [x] Enums: RoleType, UserStatus, PunchType, AttendanceStatus
- [x] Flyway migrations, indexes, FKs
- [x] JWT auth, RBAC, CORS
- [x] Required REST APIs (auth, departments, teams, users, punches, attendance, planning, dashboard)
- [x] Planning integration (mock + real client), grace period, attendance calculation
- [x] Punch validation (order, duplicates, START_WORK required)
- [x] Global exception handling, validation
- [x] Unit and integration tests (JUnit 5, Mockito, Testcontainers)
- [x] Postman collection and environment
- [x] Next.js frontend (login, dashboard, departments, teams, employees, punch, attendance)
- [x] Docker Compose for PostgreSQL
- [x] Seed/demo data (dev profile)
- [x] README and backend README

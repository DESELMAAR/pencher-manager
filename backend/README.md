# Pencher Manager – Backend

Spring Boot REST API for workforce attendance and hierarchy management.

## Stack

- **Spring Boot 3.4.x** (Java 17) – compatible with JDK 17–25
- **Maven**
- **PostgreSQL**
- **Flyway** – migrations
- **JWT** – authentication
- **Spring Security** – role-based access
- **Springdoc OpenAPI** – Swagger UI at `/swagger-ui.html`
- **JUnit 5 + Mockito + Testcontainers** – tests

## Quick start

1. **Start PostgreSQL** (Docker):

   ```bash
   docker-compose -f ../docker-compose-pencheryml up -d
   ```

2. **Run the app** (with seed data):

   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. **API**

   - Base URL: `http://localhost:8080`
   - Swagger: `http://localhost:8080/swagger-ui.html`
   - Seed login: `superadmin@pencher.com` / `SuperAdmin123!`

## Configuration

- `application.properties` – main config
- `application-dev.properties` – local DB
- JWT secret: set `JWT_SECRET` in production
- Grace period (attendance): `pencher.attendance.grace-period-minutes=5`
- Planning API: `pencher.planning.use-mock=true` uses mock; set `false` and `PLANNING_API_URL` for real API

## Tests

```bash
mvn test
```

- Unit: `AttendanceStatusCalculationTest`, `JwtServiceTest`
- Integration: `AuthControllerIntegrationTest` (Testcontainers + PostgreSQL)

## API overview

- **Auth**: `POST /api/auth/login`, `/register`, `/refresh`, `GET /api/auth/me`
- **Departments**: CRUD + `GET /api/departments/{id}/teams`
- **Teams**: CRUD + `GET /api/teams/{teamId}/employees`
- **Users**: CRUD + `PATCH /api/users/{id}/status`
- **Punches**: `POST /api/punches/start-work`, `/break-1`, `/break-2`, `/lunch-break`, `/end-shift`, `GET /api/punches/me/today`, …
- **Attendance**: `GET /api/attendance/me/today`, `/summary`, …
- **Planning**: `GET /api/planning/employee/{id}`, `POST /api/planning/sync/{id}`, `/sync-all`
- **Dashboard**: `GET /api/dashboard/super-admin`, `/department-admin`, `/team-leader`, `/employee`

## Postman

Import from `../postman/`:

- **Pencher-Manager-API.postman_collection.json**
- **Pencher-Manager.postman_environment.json**

Set `baseUrl` to `http://localhost:8080`. Use **Auth → Login** to get a token (saved in `accessToken`).

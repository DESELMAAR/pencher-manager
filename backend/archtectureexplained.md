# Pencher Manager Architecture Explained (Step by Step)

This file explains how your backend endpoints are structured, with a deep focus on the **login endpoint**:

- `POST /api/auth/login`

---

## 1) Global endpoint architecture (all APIs)

Every endpoint follows this layered flow:

1. **Controller layer** (`controller/*Controller`)
   - Receives HTTP request (`@GetMapping`, `@PostMapping`, etc.).
   - Validates request DTO with `@Valid`.
   - Calls service methods.
   - Returns `ResponseEntity<?>`.

2. **Service layer** (`service/*`, `service/impl/*Impl`)
   - Holds business logic and authorization checks.
   - Orchestrates repositories, mappers, security helpers.
   - Throws domain exceptions (`BadRequestException`, `ForbiddenException`, etc.).

3. **Repository layer** (`repository/*Repository`)
   - Data access through Spring Data JPA.
   - Uses generated queries and custom `@Query` methods.

4. **Entity layer** (`entity/*`)
   - JPA models (`User`, `Department`, `Team`, `PunchEvent`, etc.).
   - Mapped to PostgreSQL tables created by Flyway migrations.

5. **DTO + Mapper layer** (`dto/*`, `mapper/*`)
   - Controllers exchange DTOs only.
   - Mappers convert entity <-> response DTO.
   - Password/entity internals are not exposed directly.

6. **Security layer** (`security/*`, `config/SecurityConfig`)
   - JWT filter authenticates request from `Authorization: Bearer ...`.
   - Role is mapped to Spring authorities (`ROLE_SUPER_ADMIN`, etc.).
   - Protected routes and role checks are enforced in security + services.

7. **Exception layer** (`exception/GlobalExceptionHandler`)
   - Converts exceptions into standard JSON error responses.
   - Handles validation errors, unauthorized/forbidden, bad requests, and generic 500.

---

## 2) Request lifecycle for a protected endpoint

Example: `GET /api/users`

1. HTTP request enters Spring (`DispatcherServlet`).
2. `JwtAuthenticationFilter` checks Bearer token.
3. If token valid: user ID + role are put in `SecurityContext`.
4. Controller method executes.
5. Controller calls service.
6. Service applies business/role checks, calls repository.
7. Repository fetches data from DB.
8. Service maps entities to DTOs.
9. Controller returns JSON response.
10. If any error occurs, `GlobalExceptionHandler` builds error JSON.

---

## 3) Login endpoint deep dive: `POST /api/auth/login`

### 3.1 HTTP contract

- **URL:** `/api/auth/login`
- **Method:** `POST`
- **Auth required:** No (public endpoint)
- **Request body (`LoginRequest`):**
  - `email`
  - `password`
- **Response body (`TokenResponse`):**
  - `accessToken`
  - `refreshToken`
  - `expiresIn`
  - `tokenType` (`Bearer`)

---

### 3.2 Step-by-step through layers

#### Step A - Controller (`AuthController.login`)

File: `backend/src/main/java/com/pencher/manager/controller/AuthController.java`

1. Receives `POST /api/auth/login`.
2. Reads JSON into `LoginRequest`.
3. Validates required fields (`@Valid`, `@NotBlank` in DTO).
4. Calls `authService.login(request)`.
5. Returns `200 OK` with `TokenResponse`.

If email/password are missing, validation fails and `GlobalExceptionHandler` returns `400`.

---

#### Step B - Service (`AuthServiceImpl.login`)

File: `backend/src/main/java/com/pencher/manager/service/impl/AuthServiceImpl.java`

1. Uses `AuthenticationManager.authenticate(...)` with:
   - username = email
   - password = raw password from request
2. Spring Security delegates to `CustomUserDetailsService` and `PasswordEncoder` (BCrypt).
3. If authentication fails, service throws `BadRequestException("Invalid email or password")`.
4. If authentication succeeds:
   - Loads user from `UserRepository.findByEmail(...)`.
   - Generates JWT access token + refresh token via `JwtService`.
5. Builds and returns `TokenResponse`.

This means login does both:
- identity verification (email/password),
- token issuing (JWT for next calls).

---

#### Step C - UserDetails lookup (`CustomUserDetailsService`)

File: `backend/src/main/java/com/pencher/manager/security/CustomUserDetailsService.java`

1. `loadUserByUsername(email)` queries `UserRepository.findByEmail(email)`.
2. If user not found -> `UsernameNotFoundException`.
3. If status is `INACTIVE` or `SUSPENDED` -> rejects login.
4. Returns Spring Security `UserDetails` with:
   - stored BCrypt hash
   - role authority (`ROLE_*`)

---

#### Step D - Password check (`BCryptPasswordEncoder`)

Configured in: `backend/src/main/java/com/pencher/manager/config/SecurityConfig.java`

- Stored hash from DB is compared to request raw password using BCrypt.
- Plain text password is never stored.

---

#### Step E - Repository + Database

Repository: `backend/src/main/java/com/pencher/manager/repository/UserRepository.java`

Key query:
- `findByEmail(String email)`

Database table:
- `users` (`email`, `password`, `status`, `role`, ...)

---

#### Step F - JWT generation (`JwtService`)

File: `backend/src/main/java/com/pencher/manager/security/JwtService.java`

Creates:

1. **Access token**
   - subject = user ID
   - claims include `email`, `role`, `type=access`
   - short expiration (`pencher.jwt.access-expiration-ms`)

2. **Refresh token**
   - subject = user ID
   - claim `type=refresh`
   - longer expiration (`pencher.jwt.refresh-expiration-ms`)

Both are signed with `pencher.jwt.secret`.

---

### 3.3 What happens after login

For all protected endpoints:

1. Client sends `Authorization: Bearer <accessToken>`.
2. `JwtAuthenticationFilter` parses token and validates signature/expiration.
3. User ID + role authority are injected into SecurityContext.
4. Controllers/services can resolve current user via `CurrentUser`.

---

## 4) How other endpoint groups fit the same architecture

### Auth endpoints

- Controller: `AuthController`
- Service: `AuthServiceImpl`
- Security: `JwtService`, `CustomUserDetailsService`
- Repository: `UserRepository`

### Department/Team/User management

- Controllers: `DepartmentController`, `TeamController`, `UserController`
- Services: `DepartmentServiceImpl`, `TeamServiceImpl`, `UserServiceImpl`
- Repositories: `DepartmentRepository`, `TeamRepository`, `UserRepository`
- Business checks: role + scope (department/team ownership)

### Punch and attendance

- Controllers: `PunchController`, `AttendanceController`
- Services: `PunchServiceImpl`, `AttendanceServiceImpl`
- Repositories: `PunchEventRepository`, `AttendanceRecordRepository`
- Rules:
  - no invalid punch order,
  - no duplicate same punch type/day,
  - attendance status from planning + start punch time.

### Planning integration

- Controller: `PlanningController`
- Service: `PlanningServiceImpl`
- Integration clients:
  - `MockPlanningApiClient`
  - `RealPlanningApiClient`

### Dashboard

- Controller: `DashboardController`
- Service: `DashboardServiceImpl`
- Aggregates counts from repositories by role scope.

---

## 5) Error flow architecture

Any exception from service/controller is centralized in:

- `backend/src/main/java/com/pencher/manager/exception/GlobalExceptionHandler.java`

This ensures consistent JSON errors:
- timestamp
- status
- error
- message
- path
- validation field errors (if any)

---

## 6) Short sequence diagram for login

1. Client -> `AuthController.login`
2. `AuthController` -> `AuthServiceImpl.login`
3. `AuthServiceImpl` -> `AuthenticationManager.authenticate`
4. `AuthenticationManager` -> `CustomUserDetailsService.loadUserByUsername`
5. `CustomUserDetailsService` -> `UserRepository.findByEmail`
6. DB -> `UserRepository` -> `CustomUserDetailsService`
7. BCrypt password check inside auth pipeline
8. `AuthServiceImpl` -> `JwtService.generateAccessToken/generateRefreshToken`
9. `AuthServiceImpl` -> `TokenResponse`
10. `AuthController` -> Client (`200 OK`)

---

## 7) Why this architecture is good

- Clean separation of concerns.
- DTO boundary keeps entities internal.
- Security is centralized and consistent.
- Error handling is standardized.
- Business rules are testable in service layer.
- Repository code stays focused on data access.


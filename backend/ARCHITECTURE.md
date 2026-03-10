# Spring Boot API Architecture — GET Users & POST New User

This guide explains how a typical Spring Boot REST API is structured, using **GET users** and **POST new user** as examples. Every layer and step is described so you can see how a request flows from HTTP to the database and back.

---

## 1. The Big Picture: Layers

A Spring Boot REST API is usually organized in **layers**. Each layer has a single responsibility:

```
  HTTP Request
       │
       ▼
┌─────────────────────────────────────────────────────────────┐
│  CONTROLLER (REST / Presentation)  ←  Receives HTTP, returns JSON
└─────────────────────────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────────────────────────┐
│  SERVICE (Business Logic)         ←  Rules, validation, orchestration
└─────────────────────────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────────────────────────┐
│  REPOSITORY (Data Access)         ←  Talks to the database
└─────────────────────────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────────────────────────┐
│  ENTITY (Model / Domain)          ←  Represents one row in the DB
└─────────────────────────────────────────────────────────────┘
       │
       ▼
  Database (e.g. H2, MySQL)
```

**Rule of thumb:** Controller → Service → Repository. The Controller never talks to the database directly; it always goes through the Service.

---

## 2. Layer-by-Layer Explanation

### Layer 1: Entity (Model / Domain)

**File:** `entity/User.java`

**Role:** Represents **one user** as Java object. JPA maps this class to a table; each field becomes a column.

- `@Entity` — This class is a JPA entity (table).
- `@Table(name = "users")` — Table name in the DB.
- `@Id` + `@GeneratedValue` — Primary key, auto-generated.
- `@Column`, `@NotBlank`, `@Email` — Column definition and validation.

So: **Entity = one row in the database**, in Java form.

---

### Layer 2: Repository (Data Access)

**File:** `repository/UserRepository.java`

**Role:** **Access to the database**. You don’t write SQL; Spring Data JPA generates it from the interface.

- Extends `JpaRepository<User, Long>` — entity type and ID type.
- Methods like `findAll()`, `findById(id)`, `save(user)` come from `JpaRepository`.
- `findByNameContainingIgnoreCase(String name)` — Spring generates the query from the method name.

So: **Repository = “give me users from / save users to the database”.**

---

### Layer 3: Service (Business Logic)

**File:** `service/UserService.java`

**Role:** **Business rules and orchestration**. The controller calls the service; the service uses the repository.

- `getAllUsers()` — Asks the repository for all users.
- `getUserById(id)` — Asks for one user by id (returns `Optional`).
- `createUser(user)` — Saves the user via repository; `@Transactional` keeps the operation in one transaction.

So: **Service = “what the app does” (e.g. get users, create user), using the repository.**

---

### Layer 4: Controller (REST API / Presentation)

**File:** `controller/UserController.java`

**Role:** **HTTP in, HTTP out**. Maps URLs and HTTP methods to service methods, returns JSON.

- `@RestController` — This class handles REST requests and returns data (e.g. JSON).
- `@RequestMapping("/api/users")` — All endpoints start with `/api/users`.
- **GET /api/users** — `getUsers()` → calls `userService.getAllUsers()` → returns list of users (200 OK).
- **GET /api/users/{id}** — `getUserById(id)` → calls `userService.getUserById(id)` → returns one user (200) or 404.
- **POST /api/users** — `createUser(@RequestBody User user)` → validates with `@Valid`, calls `userService.createUser(user)` → returns created user with status 201 CREATED.

So: **Controller = “this URL + method → call this service method and return this HTTP response”.**

---

## 3. Step-by-Step: GET All Users

**Request:** `GET http://localhost:8080/api/users`

| Step | Where | What happens |
|------|--------|----------------|
| 1 | **DispatcherServlet** (Spring) | Receives HTTP GET, finds the controller method for `GET /api/users`. |
| 2 | **UserController.getUsers()** | Method runs; no request body or path variable. |
| 3 | **UserService.getAllUsers()** | Controller calls the service. |
| 4 | **UserRepository.findAll()** | Service calls the repository. |
| 5 | **Database** | JPA runs `SELECT * FROM users`, returns rows. |
| 6 | **Repository → Service** | Returns `List<User>`. |
| 7 | **Service → Controller** | Returns same list. |
| 8 | **Controller → Client** | Spring serializes list to JSON and sends **200 OK** with JSON body. |

So: **Client → Controller → Service → Repository → DB**, then back the same way.

---

## 4. Step-by-Step: POST New User

**Request:** `POST http://localhost:8080/api/users`  
**Body (JSON):** `{ "name": "Jane", "email": "jane@example.com" }`

| Step | Where | What happens |
|------|--------|----------------|
| 1 | **DispatcherServlet** | Receives POST, finds method for `POST /api/users`. |
| 2 | **UserController.createUser(@RequestBody User user)** | Spring deserializes JSON into a `User` object (name, email set; id null). |
| 3 | **Validation (@Valid)** | Checks `@NotBlank`, `@Email` on the entity. If invalid → **400 Bad Request**. |
| 4 | **UserService.createUser(user)** | Controller passes the user to the service. |
| 5 | **UserRepository.save(user)** | Service calls save. |
| 6 | **Database** | JPA runs `INSERT INTO users (name, email) VALUES (?, ?)`, DB generates `id`. |
| 7 | **Repository → Service** | Returns the saved `User` (with `id` set). |
| 8 | **Service → Controller** | Returns that user. |
| 9 | **Controller** | `ResponseEntity.status(HttpStatus.CREATED).body(created)` → **201 CREATED** with user JSON. |

So: **JSON in → Controller (validation) → Service → Repository → DB → User with id back → 201 + JSON.**

---

## 5. How to Try It

1. **Start the app:** run `DemoApplication` or `mvn spring-boot:run`.
2. **GET all users:**  
   `GET http://localhost:8080/api/users`  
   (Initially empty list `[]`.)
3. **POST a user:**  
   `POST http://localhost:8080/api/users`  
   Body (JSON): `{ "name": "Alice", "email": "alice@example.com" }`  
   You should get **201** and the created user with an `id`.
4. **GET all users again:**  
   `GET http://localhost:8080/api/users`  
   You should see the user you created.
5. **GET one user:**  
   `GET http://localhost:8080/api/users/1`  
   (Use the id returned from POST.)

You can use Postman, curl, or the browser (for GET only). H2 console is at `http://localhost:8080/h2-console` if you want to inspect the `users` table.

---

## 6. Summary

| Layer | Responsibility | In this example |
|-------|----------------|------------------|
| **Entity** | Model one row in DB | `User` with id, name, email |
| **Repository** | Data access (no SQL by hand) | `UserRepository` — findAll, findById, save |
| **Service** | Business logic | `UserService` — getAllUsers, getUserById, createUser |
| **Controller** | REST API | `UserController` — GET /api/users, GET /api/users/{id}, POST /api/users |

**Flow:** HTTP request → Controller → Service → Repository → Database → back through the same layers → HTTP response (JSON).

This is the standard Spring Boot API architecture: clear layers, single responsibility, and a simple path from “GET users” or “POST new user” to the database and back.

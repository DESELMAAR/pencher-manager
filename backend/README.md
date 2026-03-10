# Spring Boot Users API (GET & POST)

A minimal Spring Boot REST API to **get all users** and **create a new user**. Uses in-memory H2 database — no installation needed.

## Run the application

**Default (H2 in-memory):**
```bash
mvn spring-boot:run
```

**With PostgreSQL in Docker:** see **[DOCKER-POSTGRES.md](DOCKER-POSTGRES.md)**. Short version:
```bash
docker-compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

Server runs at **http://localhost:8080**.

## Try the API

**Get all users (empty at start):**
```bash
curl http://localhost:8080/api/users
```

**Create a user:**
```bash
curl -X POST http://localhost:8080/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Alice\",\"email\":\"alice@example.com\"}"
```

**Get all users again** — you'll see the new user with an `id`.

## Understand the architecture

- **[ARCHITECTURE.md](ARCHITECTURE.md)** — Layers (Entity → Repository → Service → Controller) and flow for GET/POST.
- **[DOCKER-POSTGRES.md](DOCKER-POSTGRES.md)** — PostgreSQL on Docker: what each step and file does.

## Project structure

```
src/main/java/com/example/demo/
├── DemoApplication.java      # Entry point
├── entity/User.java         # Layer 1: Database model
├── repository/UserRepository.java   # Layer 2: Data access
├── service/UserService.java        # Layer 3: Business logic
└── controller/UserController.java  # Layer 4: REST API
```

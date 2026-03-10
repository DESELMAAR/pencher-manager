# PostgreSQL on Docker — Step-by-Step Guide

This guide explains how PostgreSQL runs in Docker and how your Spring Boot app connects to it. Every step and file is explained so you understand what each part does.

---

## 1. What You Need to Know First

### What is Docker?

**Docker** runs software in **containers**. A container is a lightweight, isolated environment that has everything needed to run one application (e.g. PostgreSQL):

- You don’t install PostgreSQL on your Windows machine.
- You run a **PostgreSQL image** inside a container; that container behaves like a small “virtual machine” that only runs the database.
- Your Spring Boot app (running on your PC) connects to that database over the network (localhost:5432).

### What is Docker Compose?

**Docker Compose** uses a single file (`docker-compose.yml`) to define and start one or more containers. Instead of typing long `docker run` commands, you run `docker-compose up` and it reads the file and starts the right containers with the right settings.

---

## 2. What Was Added to Your Project

| Item | Role |
|------|------|
| **docker-compose.yml** | Defines the PostgreSQL container (image, port, user, password, volume). |
| **pom.xml** | Adds the **PostgreSQL JDBC driver** so Spring can talk to PostgreSQL. |
| **application-postgres.properties** | Spring config when using PostgreSQL (URL, user, password, dialect). |
| **Profile `postgres`** | Lets you choose “use PostgreSQL” without changing the default H2 setup. |

Default (no profile) = H2 in-memory. With profile `postgres` = PostgreSQL in Docker.

---

## 3. Step-by-Step: Understanding docker-compose.yml

Open `docker-compose.yml` in the project root. Here is what each part does.

```yaml
services:
  postgres:
```

- **services:** list of containers to run.
- **postgres:** name of this service (you could have more, e.g. `redis`). You’ll refer to it with `docker-compose up`, `docker-compose stop postgres`, etc.

```yaml
    image: postgres:16-alpine
```

- **image:** which Docker image to use. Here: official PostgreSQL version 16, “alpine” variant (smaller).
- Docker downloads it once from Docker Hub; then it’s cached.

```yaml
    container_name: learning-postgres
```

- **container_name:** the name of the running container. Optional; if you omit it, Docker Compose generates a name. Giving a fixed name makes it easier to recognize in `docker ps` or in the Docker Desktop UI.

```yaml
    environment:
      POSTGRES_DB: learningdb
      POSTGRES_USER: appuser
      POSTGRES_PASSWORD: apppass
```

- **environment:** variables that the PostgreSQL image reads when the container **starts for the first time**:
  - **POSTGRES_DB:** creates a database named `learningdb`.
  - **POSTGRES_USER:** creates user `appuser`.
  - **POSTGRES_PASSWORD:** sets the password to `apppass`.
- These are used only at first initialization; after that the database and user already exist.

```yaml
    ports:
      - "5432:5432"
```

- **ports:** “host:container”. Your machine’s port **5432** is mapped to the container’s port **5432**.
- So when Spring Boot connects to `localhost:5432`, the request goes to the PostgreSQL process inside the container.
- **5432** is the default PostgreSQL port.

```yaml
    volumes:
      - postgres_data:/var/lib/postgresql/data
```

- **volumes:** persistent storage.
- **postgres_data** is a named volume (defined at the bottom of the file). Docker keeps the database files there.
- **/var/lib/postgresql/data** is the path **inside the container** where PostgreSQL stores its data.
- So: “Store the container’s database files in the volume `postgres_data`.” If you remove the container but not the volume, your data stays. If you run `docker-compose up` again, the same data is reused.

```yaml
volumes:
  postgres_data:
```

- Declares the named volume **postgres_data**. Docker creates it on first run and reuses it on later runs.

---

## 4. Step-by-Step: How Spring Boot Connects (application-postgres.properties)

When you run the app with **profile `postgres`**, Spring loads `application-postgres.properties` and uses these settings instead of (or in addition to) `application.properties`.

| Property | Meaning |
|----------|--------|
| **spring.datasource.url** | JDBC URL: `jdbc:postgresql://localhost:5432/learningdb` → connect to host `localhost`, port `5432`, database `learningdb`. Must match `POSTGRES_DB` and the `ports` in docker-compose. |
| **spring.datasource.username** | User name. Must match **POSTGRES_USER** in docker-compose (`appuser`). |
| **spring.datasource.password** | Password. Must match **POSTGRES_PASSWORD** in docker-compose (`apppass`). |
| **spring.datasource.driver-class-name** | Tells Spring to use the PostgreSQL JDBC driver (from `pom.xml`). |
| **spring.jpa.database-platform** | Hibernate dialect for PostgreSQL (generates correct SQL for Postgres). |
| **spring.jpa.hibernate.ddl-auto=update** | On startup, Hibernate **updates** the schema (adds tables/columns if needed). It does **not** drop tables. Good for development with a real DB. |
| **spring.jpa.show-sql** | Logs SQL in the console so you can see what JPA does. |

So: **same credentials and database name** in both Docker and Spring = connection works.

---

## 5. Why the PostgreSQL Driver in pom.xml?

Spring Boot’s **spring-boot-starter-data-jpa** does not include the PostgreSQL driver. So we add:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

- **runtime:** the driver is needed only when the app runs (and connects to PostgreSQL), not at compile time. Your code only uses the generic `javax.sql` / JDBC and JPA APIs.
- Without this dependency, at startup you’d get an error like “No suitable driver for jdbc:postgresql”.

---

## 6. Order of Operations: What to Do When

### First time (or after pulling the project)

1. **Install Docker**
   - Install [Docker Desktop for Windows](https://docs.docker.com/desktop/install/windows-install/).
   - Start Docker Desktop and wait until it’s running (whale icon in the system tray).

2. **Start PostgreSQL**
   - Open a terminal in the **project root** (where `docker-compose.yml` is).
   - Run:
     ```bash
     docker-compose up -d
     ```
   - **-d** = “detached”: the container runs in the background.
   - First time: Docker downloads the image, then creates the container and the database `learningdb` with user `appuser` and password `apppass`.

3. **Run your Spring Boot app with PostgreSQL**
   - In the same project (from IDE or terminal):
     ```bash
     mvn spring-boot:run -Dspring-boot.run.profiles=postgres
     ```
   - Or in the IDE: set **Active profiles** to `postgres` in your run configuration, then run `DemoApplication`.
   - The app will connect to `localhost:5432/learningdb` and start; Hibernate will create/update the `users` table.

4. **Use the API**
   - Same as before: `GET /api/users`, `POST /api/users` with JSON body. Data is now stored in PostgreSQL in Docker.

### Every other time

1. Start Docker Desktop (if not running).
2. In project root: `docker-compose up -d` (if the container was stopped).
3. Run the app with profile **postgres**.

### Stopping and removing (optional)

- Stop containers: `docker-compose stop`
- Stop and remove containers (data in volume remains): `docker-compose down`
- Remove containers and the database volume (deletes all data): `docker-compose down -v`

---

## 7. Quick Reference

| Goal | Command / Action |
|------|-------------------|
| Start PostgreSQL (background) | `docker-compose up -d` |
| See if Postgres is running | `docker ps` (look for `learning-postgres`) |
| Run app with PostgreSQL | `mvn spring-boot:run -Dspring-boot.run.profiles=postgres` or set profile `postgres` in IDE |
| Stop PostgreSQL | `docker-compose stop` |
| View container logs | `docker-compose logs -f postgres` |

---

## 8. Summary

- **Docker** runs PostgreSQL in a container; **Docker Compose** starts it from `docker-compose.yml`.
- **docker-compose.yml** defines: image, port 5432, database name, user, password, and a volume so data persists.
- **application-postgres.properties** tells Spring Boot how to connect (URL, user, password, driver, dialect).
- **pom.xml** adds the PostgreSQL JDBC driver so the JVM can connect to PostgreSQL.
- **Order:** start Docker → `docker-compose up -d` → run the app with profile **postgres**. Then your GET/POST users API uses PostgreSQL in Docker.

Once this is clear, you can change database name, user, or password in both `docker-compose.yml` and `application-postgres.properties` (keep them in sync) and reuse the same pattern for other projects.

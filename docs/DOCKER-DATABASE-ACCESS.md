# How to Access pencherdb via Docker Shell

This guide explains how to open a shell inside the PostgreSQL Docker container and connect to the **pencherdb** database.

---

## Prerequisites

- **Docker** is installed and running.
- The Pencher PostgreSQL container is running (started with `docker-compose -f docker-compose-pencher.yml up -d`).

---

## Step 1: Start the database (if not already running)

From the project root:

```bash
docker-compose -f docker-compose-pencher.yml up -d
```

Check that the container is up:

```bash
docker ps
```

You should see a container named **pencher-postgres** (or similar) using image `postgres:16-alpine` and port `5432`.

---

## Step 2: Open a shell inside the container

Use `docker exec` to run a shell in the running PostgreSQL container.

**If you know the container name (pencher-postgres):**

```bash
docker exec -it pencher-postgres sh
```

**If you're not sure of the name**, list running containers:

```bash
docker ps
```

Then use the **CONTAINER ID** (first column) or **NAME** (last column):

```bash
docker exec -it <CONTAINER_ID_OR_NAME> sh
```

Example with container ID:

```bash
docker exec -it a1b2c3d4e5f6 sh
```

- `-it` = interactive terminal (you can type commands).
- `sh` = shell to run (Alpine image uses `sh`).

You should see a prompt like:

```
/ #
```

You are now **inside** the container.

---

## Step 3: Connect to PostgreSQL (psql)

Inside the container, use `psql` to connect to **pencherdb** as user **appuser**:

```bash
psql -U appuser -d pencherdb
```

- `-U appuser` = username  
- `-d pencherdb` = database name  

When prompted for a password, enter: **apppass**

You should see something like:

```
psql (16.x)
Type "help" for help.

pencherdb=#
```

`pencherdb=#` means you are connected to the **pencherdb** database.

---

## Step 4: Useful commands inside psql

| Command | Description |
|--------|-------------|
| `\l` | List all databases |
| `\dt` | List tables in the current database |
| `\d table_name` | Describe a table (columns, types) |
| `\du` | List database roles/users |
| `\q` | Quit psql |

**Example: list tables**

```sql
\dt
```

**Example: describe the `users` table**

```sql
\d users
```

**Example: run a query**

```sql
SELECT id, email, full_name, role FROM users LIMIT 5;
```

**Example: quit psql**

```sql
\q
```

After `\q` you are back in the container shell (`/ #`).

---

## Step 5: Exit the container shell

When you're done and back at the container prompt (`/ #`), exit the shell:

```bash
exit
```

You are back in your normal terminal (e.g. PowerShell or bash).

---

## One-liner: shell + psql in one command

You can skip opening a generic shell and go straight into **psql** inside the container:

```bash
docker exec -it pencher-postgres psql -U appuser -d pencherdb
```

You may be prompted for the password: **apppass**.

To pass the password so you're not prompted (optional):

**Linux/macOS/Git Bash:**

```bash
docker exec -it pencher-postgres env PGPASSWORD=apppass psql -U appuser -d pencherdb
```

**PowerShell (Windows):**

```powershell
docker exec -it pencher-postgres sh -c "PGPASSWORD=apppass psql -U appuser -d pencherdb"
```

Then use psql commands as in Step 4, and type `\q` to quit.

---

## Summary (quick copy-paste)

```bash
# 1. Start DB
docker-compose -f docker-compose-pencher.yml up -d

# 2. Open shell in container
docker exec -it pencher-postgres sh

# 3. Inside container: connect to pencherdb (password: apppass)
psql -U appuser -d pencherdb

# 4. In psql: list tables
\dt

# 5. Quit psql then exit container
\q
exit
```

**Or in one command:**

```bash
docker exec -it pencher-postgres psql -U appuser -d pencherdb
```

Password when prompted: **apppass**.

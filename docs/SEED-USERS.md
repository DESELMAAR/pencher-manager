# How to Seed Test Users (Super Admin, Admin, etc.)

You can create fictitious users to test the API in **two ways**: run the backend with the **dev** profile (Java seeder), or run a **SQL script** in the database.

---

## Option 1: Run the backend with the dev profile (recommended)

The app already has a **DataSeeder** that creates one Super Admin, one Department Admin, one Team Leader, and one Employee, with **strong passwords**.

### Steps

1. **Start the database** (if not already running):
   ```bash
   docker-compose -f docker-compose-pencher.yml up -d
   ```

2. **Run the backend with the `dev` profile:**
   ```bash
   cd backend
   mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
   ```
   On **PowerShell**, use quotes around the `-D` argument:
   ```powershell
   mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
   ```

3. On first startup, the seeder creates:
   - **Department:** IT  
   - **Team:** Development  
   - **Users:** see table below  

4. **Stop the app** (Ctrl+C), then start it again **without** the profile for normal use:
   ```bash
   mvn spring-boot:run
   ```

### Seed users (Option 1 – Java seeder)

| Role             | Email                    | Password        |
|------------------|--------------------------|-----------------|
| Super Admin      | superadmin@pencher.com   | SuperAdmin123!  |
| Department Admin | deptadmin@pencher.com    | DeptAdmin123!   |
| Team Leader      | teamleader@pencher.com   | TeamLeader123!  |
| Employee         | employee@pencher.com     | Employee123!    |

---

## Option 2: Run the SQL seed script in Docker/psql

If you prefer to seed **directly in the database** (e.g. you already have tables but no users), use the SQL script. All seed users get the **same password: `password`**.

### Steps

1. **Start the database** (if not already running):
   ```bash
   docker-compose -f docker-compose-pencher.yml up -d
   ```

2. **Connect to PostgreSQL** in the container (see [DOCKER-DATABASE-ACCESS.md](./DOCKER-DATABASE-ACCESS.md)):
   ```bash
   docker exec -it pencher-postgres psql -U appuser -d pencherdb
   ```
   Password: **apppass**

3. **Run the seed script** from inside psql.

   **If you are already in psql** (prompt `pencherdb=#`):
   ```sql
   \i /path/to/seed-test-users.sql
   ```
   The file is inside your project, so you must **copy it into the container** or run it from the host.

   **Easier: run the script from the host** (one command):
   ```bash
   docker exec -i pencher-postgres psql -U appuser -d pencherdb < backend/src/main/resources/db/seed/seed-test-users.sql
   ```
   From the **project root** (where `backend` and `docker-compose-pencher.yml` are).  
   On **PowerShell**:
   ```powershell
   Get-Content backend\src\main\resources\db\seed\seed-test-users.sql | docker exec -i pencher-postgres psql -U appuser -d pencherdb
   ```

4. If prompted for the DB password, use **apppass**. The script creates the department, team, and 4 users. It **skips** if `superadmin@pencher.com` already exists.

### Seed users (Option 2 – SQL script)

| Role             | Email                    | Password   |
|------------------|--------------------------|------------|
| Super Admin      | superadmin@pencher.com   | password   |
| Department Admin | deptadmin@pencher.com    | password   |
| Team Leader      | teamleader@pencher.com   | password   |
| Employee         | employee@pencher.com     | password   |

**Important:** For the SQL script, the password for **all** these users is the single word: **password** (lowercase).

---

## Test login

After seeding (either option), call the login endpoint:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"superadmin@pencher.com\",\"password\":\"SuperAdmin123!\"}"
```

- **Option 1 (dev profile):** use the passwords from the first table (e.g. `SuperAdmin123!`).
- **Option 2 (SQL script):** use **password** for all users.

Use the returned `accessToken` in the `Authorization: Bearer ...` header for other API calls.

---

## Summary

| Method              | How to run                                      | Passwords                    |
|---------------------|--------------------------------------------------|------------------------------|
| **Option 1 – Java** | `mvn spring-boot:run "-Dspring-boot.run.profiles=dev"` | SuperAdmin123!, DeptAdmin123!, etc. |
| **Option 2 – SQL**  | Run `seed-test-users.sql` in psql (see above)    | **password** for all         |

Script location: **`backend/src/main/resources/db/seed/seed-test-users.sql`**

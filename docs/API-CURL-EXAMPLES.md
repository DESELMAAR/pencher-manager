# Pencher Manager – cURL examples

Base URL: `http://localhost:8080`. Replace `YOUR_TOKEN` with the `accessToken` from login.

---

## 1. Login (get token)

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"superadmin@pencher.com\",\"password\":\"SuperAdmin123!\"}"
```

Save the `accessToken` from the response, then:

```bash
export TOKEN="<paste-accessToken-here>"
```

---

## 2. Auth (with token)

```bash
# Current user
curl -s -X GET http://localhost:8080/api/auth/me -H "Authorization: Bearer $TOKEN"

# Logout
curl -s -X POST http://localhost:8080/api/auth/logout -H "Authorization: Bearer $TOKEN"
```

---

## 3. Departments

```bash
# List
curl -s -X GET http://localhost:8080/api/departments -H "Authorization: Bearer $TOKEN"

# Create
curl -s -X POST http://localhost:8080/api/departments \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d "{\"name\":\"Engineering\",\"description\":\"Dev team\"}"

# Get by ID (use real id, e.g. 1)
curl -s -X GET http://localhost:8080/api/departments/1 -H "Authorization: Bearer $TOKEN"

# Get teams of department 1
curl -s -X GET http://localhost:8080/api/departments/1/teams -H "Authorization: Bearer $TOKEN"
```

---

## 4. Teams

```bash
# List
curl -s -X GET http://localhost:8080/api/teams -H "Authorization: Bearer $TOKEN"

# Create (departmentId 1)
curl -s -X POST http://localhost:8080/api/teams \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d "{\"departmentId\":1,\"name\":\"Backend\",\"description\":\"Backend team\"}"

# Get employees of team 1
curl -s -X GET http://localhost:8080/api/teams/1/employees -H "Authorization: Bearer $TOKEN"
```

---

## 5. Users

```bash
# List
curl -s -X GET http://localhost:8080/api/users -H "Authorization: Bearer $TOKEN"

# Create
curl -s -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d "{\"fullName\":\"Test User\",\"email\":\"test@test.com\",\"password\":\"Test123!\",\"role\":\"EMPLOYEE\",\"departmentId\":1,\"teamId\":1}"
```

---

## 6. Punches (as employee)

```bash
# Start work
curl -s -X POST http://localhost:8080/api/punches/start-work -H "Authorization: Bearer $TOKEN"

# My punches today
curl -s -X GET http://localhost:8080/api/punches/me/today -H "Authorization: Bearer $TOKEN"

# End shift
curl -s -X POST http://localhost:8080/api/punches/end-shift -H "Authorization: Bearer $TOKEN"
```

---

## 7. Attendance

```bash
# My attendance today
curl -s -X GET http://localhost:8080/api/attendance/me/today -H "Authorization: Bearer $TOKEN"

# Summary (adjust dates)
curl -s -X GET "http://localhost:8080/api/attendance/summary?from=2025-01-01&to=2025-12-31" -H "Authorization: Bearer $TOKEN"
```

---

## 8. Dashboard

```bash
# Super Admin (use Super Admin token)
curl -s -X GET http://localhost:8080/api/dashboard/super-admin -H "Authorization: Bearer $TOKEN"

# Employee
curl -s -X GET http://localhost:8080/api/dashboard/employee -H "Authorization: Bearer $TOKEN"
```

---

## 9. Planning

```bash
# Planning for employee 4 (use real id)
curl -s -X GET "http://localhost:8080/api/planning/employee/4?from=2025-03-01&to=2025-03-31" -H "Authorization: Bearer $TOKEN"

# Sync planning for employee 4
curl -s -X POST http://localhost:8080/api/planning/sync/4 -H "Authorization: Bearer $TOKEN"
```

---

**Full step-by-step list:** see [API-TESTING-GUIDE.md](./API-TESTING-GUIDE.md).

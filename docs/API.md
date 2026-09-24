# DevSphere API Documentation

## 1. Overview

This document contains the API endpoints implemented so far in **DevSphere**, along with request examples, authorization rules, Postman testing guidance, and the current development checklist.

---

## 2. Prerequisites

1. Start PostgreSQL.
2. Make sure the `DevSphere` database exists.
3. Start the Spring Boot application.
4. Confirm the application is running on port `8080`.
5. Open Postman.
6. Use `http://localhost:8080` as the base URL.

> If your application uses another port, replace `8080` accordingly.

---

## 3. Base URL

```text
http://localhost:8080
```

Recommended Postman collection variable:

```text
baseUrl = http://localhost:8080
```

Then use:

```text
{{baseUrl}}/api/...
```

---

## 4. Authentication

Protected APIs require a JWT.

Typical flow:

```text
Register
   ↓
Login
   ↓
Copy JWT
   ↓
Use Bearer Token
   ↓
Call protected API
```

In Postman:

1. Open **Authorization**.
2. Select **Bearer Token**.
3. Paste the JWT returned by the login endpoint.

You can optionally store the JWT as a Postman variable named `accessToken`.

---

## 5. Common Response Format

### Successful response

```json
{
  "success": true,
  "message": "Operation completed successfully.",
  "data": {},
  "timestamp": "2026-09-24T20:00:00"
}
```

### Successful response without data

```json
{
  "success": true,
  "message": "Operation completed successfully.",
  "data": null,
  "timestamp": "2026-09-24T20:00:00"
}
```

### Error response

```json
{
  "success": false,
  "message": "Error description.",
  "data": null,
  "timestamp": "2026-09-24T20:00:00"
}
```

---

# 6. Authentication APIs

## 6.1 Register User

Creates a new user account.

**POST**

```text
{{baseUrl}}/api/auth/register
```

**Authentication:** None

### Request body

```json
{
  "username": "Kaustubh",
  "email": "kaustubh@example.com",
  "password": "password123",
  "confirmPassword": "password123"
}
```

### Postman steps

1. Create a `POST` request.
2. Enter the endpoint.
3. Select **Body → raw → JSON**.
4. Enter the request body.
5. Click **Send**.

The password is stored using BCrypt hashing.

---

## 6.2 Login

Authenticates a user and returns a JWT.

**POST**

```text
{{baseUrl}}/api/auth/login
```

**Authentication:** None

### Request body

```json
{
  "email": "kaustubh@example.com",
  "password": "password123"
}
```

### Postman steps

1. Create a `POST` request.
2. Enter the endpoint.
3. Select **Body → raw → JSON**.
4. Enter valid credentials.
5. Click **Send**.
6. Copy the JWT from the response.
7. Use it as a Bearer token for protected APIs.

---

## 6.3 Protected Hello

Used to verify JWT authentication.

**GET**

```text
{{baseUrl}}/api/auth/hello
```

**Authentication:** Required

### Postman steps

1. Create a `GET` request.
2. Enter the endpoint.
3. Open **Authorization**.
4. Select **Bearer Token**.
5. Paste the JWT obtained from Login.
6. Click **Send**.

### Negative test

Remove the Authorization header and send again.

Expected result:

```text
HTTP 401
```

with an authentication-required error.

---

# 7. Organization APIs

## 7.1 Create Organization

Creates an organization for the authenticated user.

The creator becomes the `OWNER`, and an owner membership is created automatically.

**POST**

```text
{{baseUrl}}/api/organizations
```

**Authentication:** Required

### Request body

```json
{
  "name": "DevSphere",
  "description": "Collaborative development workspace."
}
```

### Response

The response contains organization information such as:

- `id`
- `name`
- `description`
- `ownerId`
- `ownerUsername`
- `createdAt`
- `updatedAt`

Passwords must never be returned.

---

## 7.2 Get Organization By ID

**GET**

```text
{{baseUrl}}/api/organizations/{organizationId}
```

Example:

```text
{{baseUrl}}/api/organizations/1
```

**Authentication:** Required

Only an authenticated organization member should be able to access the organization.

---

## 7.3 Get My Organizations

Returns organizations owned by the authenticated user.

**GET**

```text
{{baseUrl}}/api/organizations
```

**Authentication:** Required

---

# 8. Organization Member APIs

## 8.1 Add Member

Adds an existing user to an organization.

**POST**

```text
{{baseUrl}}/api/organizations/{organizationId}/members
```

**Authentication:** Required

### Permission rules

```text
OWNER  → Can add ADMIN or MEMBER
ADMIN  → Can add MEMBER only
MEMBER → Cannot add anyone
```

`OWNER` cannot be assigned through this endpoint.

### Request body

```json
{
  "email": "piku@example.com",
  "role": "MEMBER"
}
```

Valid roles:

```text
OWNER
ADMIN
MEMBER
```

### Negative tests

- MEMBER attempts to add a member → reject.
- Add an existing member → reject.
- Attempt to assign `OWNER` → reject.
- ADMIN attempts to assign `ADMIN` → reject.

---

## 8.2 Get Organization Members

**GET**

```text
{{baseUrl}}/api/organizations/{organizationId}/members
```

**Authentication:** Required

Only members of the organization can view its member list.

### Example response

```json
{
  "success": true,
  "message": "Members fetched successfully.",
  "data": [
    {
      "email": "kaustubh@example.com",
      "joinedAt": "2026-08-27T20:13:54.242707",
      "role": "OWNER",
      "userId": 1,
      "username": "Kaustubh"
    },
    {
      "email": "piku@example.com",
      "joinedAt": "2026-08-31T19:56:14.486568",
      "role": "MEMBER",
      "userId": 2,
      "username": "Piku"
    }
  ],
  "timestamp": "..."
}
```

---

## 8.3 Remove Member

**DELETE**

```text
{{baseUrl}}/api/organizations/{organizationId}/members/{userId}
```

**Authentication:** Required

No request body is required.

### Permission rules

```text
OWNER  → Can remove MEMBER or ADMIN
ADMIN  → Can remove MEMBER only
MEMBER → Cannot remove anyone
OWNER  → Cannot be removed
```

### Negative tests

- MEMBER attempts removal → reject.
- ADMIN attempts to remove another ADMIN → reject.
- Attempt to remove OWNER → reject.
- Attempt to remove a non-member → appropriate error.

---

## 8.4 Update Member Role

Changes the role of an existing organization member.

**PATCH**

```text
{{baseUrl}}/api/organizations/{organizationId}/members/{userId}/role
```

**Authentication:** Required

### Permission rules

```text
OWNER  → Can change member roles
ADMIN  → Cannot change member roles
MEMBER → Cannot change member roles
```

The organization owner cannot be changed through this endpoint. Ownership transfer will be handled separately.

### Request body

```json
{
  "role": "ADMIN"
}
```

Allowed roles:

```text
ADMIN
MEMBER
```

`OWNER` must not be assigned through this endpoint.

---

# 9. Project APIs

## 9.1 Create Project

Creates a project inside an existing organization.

The authenticated user must be a member of the organization.

**POST**

```text
{{baseUrl}}/api/organizations/{organizationId}/projects
```

Example:

```text
{{baseUrl}}/api/organizations/3/projects
```

**Authentication:** Required

### Request body

```json
{
  "name": "DevSphere Backend",
  "description": "Backend project for collaborative development."
}
```

### Validation rules

- `name` is required.
- Project name must not exceed 100 characters.
- Project description must not exceed 500 characters.

### Negative tests

- Missing JWT → `401`.
- Non-member attempts to create a project → reject.
- Organization does not exist → appropriate error.
- Missing project name → reject.
- Project name longer than 100 characters → reject.
- Description longer than 500 characters → reject.

### Database verification

```sql
SELECT *
FROM projects
ORDER BY id DESC;
```

---

## 9.2 Get Project By ID

Returns a project belonging to the specified organization.

**GET**

```text
{{baseUrl}}/api/organizations/{organizationId}/projects/{projectId}
```

Example:

```text
{{baseUrl}}/api/organizations/1/projects/3
```

**Authentication:** Required

### Validation

- Project must exist.
- Project must belong to the supplied organization.
- Authenticated user must be a member of the organization.

---

## 9.3 Get Projects By Organization

Returns all projects belonging to an organization.

**GET**

```text
{{baseUrl}}/api/organizations/{organizationId}/projects
```

**Authentication:** Required

### Validation

- Organization must exist through its project/membership context.
- Authenticated user must be a member of the organization.

---

## 9.4 Update Project

Updates an existing project.

**PUT**

```text
{{baseUrl}}/api/organizations/{organizationId}/projects/{projectId}
```

**Authentication:** Required

### Request body

Both fields are optional, allowing a partial update.

```json
{
  "name": "Updated DevSphere Backend",
  "description": "Updated project description."
}
```

### Permission rules

```text
OWNER  → Can update
ADMIN  → Can update
MEMBER → Cannot update
```

### Validation

- Project must exist.
- Project must belong to the supplied organization.
- Requester must be an organization member.
- Requester must have `OWNER` or `ADMIN` role.

---

## 9.5 Delete Project

Deletes an existing project.

**DELETE**

```text
{{baseUrl}}/api/organizations/{organizationId}/projects/{projectId}
```

**Authentication:** Required

No request body is required.

### Permission rules

```text
OWNER  → Can delete
ADMIN  → Can delete
MEMBER → Cannot delete
```

### Validation

- Project must exist.
- Project must belong to the supplied organization.
- Requester must be an organization member.
- Requester must have `OWNER` or `ADMIN` role.

---

# 10. Task APIs

Tasks belong to projects.

Task structure:

```text
Organization
    └── Project
          └── Task
```

A task contains:

- `id`
- `title`
- `description`
- `status`
- `priority`
- `dueDate`
- `createdAt`
- `updatedAt`
- `project`
- `createdBy`
- `assignedTo`

### Task status values

```text
TODO
IN_PROGRESS
IN_REVIEW
DONE
```

### Task priority values

```text
LOW
MEDIUM
HIGH
URGENT
```

Default values:

```text
status   → TODO
priority → MEDIUM
```

`assignedTo` is optional because a task can be created before it is assigned.

---

## 10.1 Create Task

Creates a task inside a project.

**POST**

```text
{{baseUrl}}/api/organizations/{organizationId}/projects/{projectId}/tasks
```

**Authentication:** Required

### Request body

```json
{
  "title": "Implement task API",
  "description": "Create task creation endpoint",
  "priority": "HIGH",
  "dueDate": "2026-10-01",
  "assignedTo": 2
}
```

`assignedTo` is optional.

Example without an assignee:

```json
{
  "title": "Test task",
  "description": "Testing task creation",
  "priority": "HIGH",
  "dueDate": "2026-10-01"
}
```

### Validation rules

- `title` is required.
- Task title must not exceed 100 characters.
- Task description must not exceed 500 characters.
- `priority` is optional and defaults to `MEDIUM`.
- `dueDate` is optional.
- `assignedTo` is optional.
- The authenticated user becomes `createdBy`.
- `status` defaults to `TODO`.

### Authorization and validation

The service verifies:

1. The authenticated user exists.
2. The project exists.
3. The project belongs to the supplied organization.
4. The requester is a member of the organization.
5. If an assignee is supplied, the user exists.
6. The assignee is a member of the same organization.

### Negative tests

- Missing JWT → `401`.
- Invalid/expired JWT → reject.
- Project does not exist → reject.
- Project belongs to another organization → reject.
- Requester is not an organization member → reject.
- Assignee does not exist → reject.
- Assignee is not an organization member → reject.
- Missing task title → validation error.
- Task title longer than 100 characters → validation error.
- Description longer than 500 characters → validation error.

### Automatic timestamps

`createdAt` and `updatedAt` are populated using Spring Data JPA auditing.

---

## 10.2 Get Tasks By Project

Returns all tasks belonging to a project.

**GET**

```text
{{baseUrl}}/api/organizations/{organizationId}/projects/{projectId}/tasks
```

**Authentication:** Required

### Response

The response contains a list of `TaskResponse` objects.

Each task response contains:

```text
id
title
description
status
priority
dueDate
projectId
createdById
assignedToId
createdAt
updatedAt
```

`assignedToId` is `null` when the task has not been assigned.

### Authorization and validation

The service verifies:

1. The authenticated user exists.
2. The project exists.
3. The project belongs to the supplied organization.
4. The requester is a member of the organization.
5. Tasks are then fetched using the project ID.

### Negative tests

- Missing JWT → `401`.
- Invalid/expired JWT → reject.
- Project does not exist → reject.
- Project belongs to another organization → reject.
- Requester is not an organization member → reject.

---

## 10.3 Get Task By ID

**Planned next endpoint**

```text
GET {{baseUrl}}/api/organizations/{organizationId}/projects/{projectId}/tasks/{taskId}
```

This endpoint is not implemented yet.

---

## 10.4 Update Task

**Planned**

```text
PUT {{baseUrl}}/api/organizations/{organizationId}/projects/{projectId}/tasks/{taskId}
```

Planned fields include:

- title
- description
- status
- priority
- due date
- assignee

---

## 10.5 Delete Task

**Planned**

```text
DELETE {{baseUrl}}/api/organizations/{organizationId}/projects/{projectId}/tasks/{taskId}
```

Authorization rules will be defined when the endpoint is implemented.

---

# 11. Current API Summary

| Method | Endpoint | Auth | Status | Purpose |
|---|---|---|---|---|
| POST | `/api/auth/register` | No | ✅ | Register user |
| POST | `/api/auth/login` | No | ✅ | Login and obtain JWT |
| GET | `/api/auth/hello` | Yes | ✅ | Test JWT authentication |
| POST | `/api/organizations` | Yes | ✅ | Create organization |
| GET | `/api/organizations/{organizationId}` | Yes | ✅ | Get organization |
| GET | `/api/organizations` | Yes | ✅ | Get owned organizations |
| POST | `/api/organizations/{organizationId}/members` | Yes | ✅ | Add member |
| GET | `/api/organizations/{organizationId}/members` | Yes | ✅ | List members |
| DELETE | `/api/organizations/{organizationId}/members/{userId}` | Yes | ✅ | Remove member |
| PATCH | `/api/organizations/{organizationId}/members/{userId}/role` | Yes | ✅ | Update member role |
| POST | `/api/organizations/{organizationId}/projects` | Yes | ✅ | Create project |
| GET | `/api/organizations/{organizationId}/projects/{projectId}` | Yes | ✅ | Get project |
| GET | `/api/organizations/{organizationId}/projects` | Yes | ✅ | Get organization projects |
| PUT | `/api/organizations/{organizationId}/projects/{projectId}` | Yes | ✅ | Update project |
| DELETE | `/api/organizations/{organizationId}/projects/{projectId}` | Yes | ✅ | Delete project |
| POST | `/api/organizations/{organizationId}/projects/{projectId}/tasks` | Yes | ✅ | Create task |
| GET | `/api/organizations/{organizationId}/projects/{projectId}/tasks` | Yes | ✅ | Get project tasks |
| GET | `/api/organizations/{organizationId}/projects/{projectId}/tasks/{taskId}` | Yes | — | Get task by ID |
| PUT | `/api/organizations/{organizationId}/projects/{projectId}/tasks/{taskId}` | Yes | — | Update task |
| DELETE | `/api/organizations/{organizationId}/projects/{projectId}/tasks/{taskId}` | Yes | — | Delete task |

> The APIs marked `—` are planned and have not been implemented yet.

---

# 12. Recommended Postman Testing Sequence

For a fresh database:

```text
1. Register User A
       ↓
2. Login User A
       ↓
3. Save JWT
       ↓
4. Test protected Hello API
       ↓
5. Create Organization
       ↓
6. Get Organization
       ↓
7. Get My Organizations
       ↓
8. Register User B
       ↓
9. Login User B
       ↓
10. Add User B as MEMBER
       ↓
11. Get Organization Members
       ↓
12. Test MEMBER permissions
       ↓
13. OWNER changes MEMBER → ADMIN
       ↓
14. Test ADMIN permissions
       ↓
15. OWNER removes ADMIN/MEMBER
       ↓
16. Verify member list
       ↓
17. Create Project
       ↓
18. Get Project
       ↓
19. Get Organization Projects
       ↓
20. Update Project
       ↓
21. Test MEMBER project-update restriction
       ↓
22. Delete Project
       ↓
23. Create another Project for Task testing
       ↓
24. Create Task
       ↓
25. Create Task without assignee
       ↓
26. Get Project Tasks
       ↓
27. Verify Task data in PostgreSQL
```

---

# 13. Permission Testing Matrix

## Organization membership

| Operation | OWNER | ADMIN | MEMBER |
|---|:---:|:---:|:---:|
| View members | ✅ | ✅ | ✅ |
| Add MEMBER | ✅ | ✅ | ❌ |
| Add ADMIN | ✅ | ❌ | ❌ |
| Add OWNER | ❌ | ❌ | ❌ |
| Remove MEMBER | ✅ | ✅ | ❌ |
| Remove ADMIN | ✅ | ❌ | ❌ |
| Remove OWNER | ❌ | ❌ | ❌ |
| Change member role | ✅ | ❌ | ❌ |
| Change OWNER role | ❌ | ❌ | ❌ |
| Assign OWNER | ❌ | ❌ | ❌ |

## Project management

| Operation | OWNER | ADMIN | MEMBER |
|---|:---:|:---:|:---:|
| View project | ✅ | ✅ | ✅ |
| Create project | ✅ | ✅ | ✅ |
| Update project | ✅ | ✅ | ❌ |
| Delete project | ✅ | ✅ | ❌ |

> Project creation currently requires organization membership; update and delete are restricted to `OWNER` and `ADMIN`.

## Task management

Current implemented task operations:

| Operation | Organization Member | Non-member |
|---|:---:|:---:|
| Create task | ✅ | ❌ |
| View project tasks | ✅ | ❌ |

Task-specific update/delete permissions will be defined when those endpoints are implemented.

---

# 14. Planned / Future Features

The following features are planned but not implemented yet:

```text
Get task by ID
Update task
Delete task
Task assignment management
Task dependencies
Issue management
Comments
Activity timeline
Notifications
Sprints
Ownership transfer
GitHub integration
Developer analytics
AI project assistant
Pagination and filtering
Automated tests
Production readiness
CI/CD
```

Add each new endpoint to this document as it becomes implemented.

---

# 15. API Testing Checklist

For every new endpoint, test:

1. Successful request.
2. Missing JWT.
3. Invalid/expired JWT where applicable.
4. Invalid request body.
5. Missing required fields.
6. Resource not found.
7. Unauthorized user.
8. Forbidden operation.
9. Duplicate/conflicting operation where applicable.
10. Organization/resource relationship mismatch where applicable.
11. Database state after a successful mutation.

---

# 16. Development Convention

Whenever a new API is implemented:

```text
Implement endpoint
      ↓
Test successful case
      ↓
Test validation errors
      ↓
Test authorization rules
      ↓
Test edge cases
      ↓
Verify database state
      ↓
Update API.md
      ↓
Commit changes
```

---

# 17. Current Development Status

### Completed

- Authentication and JWT
- User registration and login
- Organization creation and retrieval
- Organization membership management
- Role-based organization permissions
- Project creation
- Project retrieval
- Project update
- Project deletion
- Task creation
- Task retrieval by project
- Task validation and authorization
- JPA auditing for task timestamps

### Current next task

Implement:

```http
GET /api/organizations/{organizationId}/projects/{projectId}/tasks/{taskId}
```

Then continue with Task Update and Task Delete.

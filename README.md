# Task Master

A team-based task management backend built with Spring Boot. Users can register, form teams, invite members, assign tasks, comment, attach files, and get real-time notifications over SSE.

## Tech Stack

- **Java 25**, **Spring Boot 4.1.1**
- **Spring Web (MVC)** — REST API
- **Spring Data JPA** + **MySQL**
- **Spring Security** — stateless, JWT-based auth
- **JJWT (0.13.0)** — JWT creation/parsing
- **MapStruct** — entity ↔ DTO mapping
- **Lombok**
- **Bean Validation** (`spring-boot-starter-validation`)
- **H2** — in-memory DB for tests

## Features

### Auth
- Register and sign in (BCrypt password hashing, strength 8)
- Stateless JWT authentication via a custom `JwtFilter`
- Logout with token blacklisting (`BlackListJwt` entity + repository)
- Public endpoints: `/api/auth/register`, `/api/auth/signIn`; everything else requires a valid JWT

### Users
- Get own profile
- Update profile
- Delete a user (admin-only, enforced with `@PreAuthorize("hasRole('ADMIN')")`)

### Teams
- Create a team
- Invite a member by email
- Accept an invite via token
- Get team details
- Paginated, sortable list of invites

### Tasks
- Create, update, delete, and fetch a task by title
- Paginated task listing with optional status filter and sort order
- Assign a task to a team member
- View tasks assigned to the current user (paginated, filterable)
- Update the status of an assigned task (`TODO`, `IN_PROGRESS`, `DUE`, `COMPLETED`)
- View all assignments across tasks (paginated)
- Add comments to a task
- Upload file attachments to a task (multipart)

### Notifications
- Real-time team notifications via Server-Sent Events (`GET /api/teams/notifications/stream`)
- Send a notification to a team member
- List notifications (filterable by team, unread-only)
- Mark a notification as read

### Cross-cutting
- Consistent API envelope via `ApiResponse<T>` for every endpoint
- Centralized exception handling (`GlobalErrorHandler`) with dedicated exceptions for cases like `UserNotFound`, `TaskNotFound`, `TeamDoesNotExist`, `TeamDuplicate`, `PasswordIncorrect`, `InvalidTokenException`, `NonAuthorized`, `NotificationNotFound`

## Project Structure

```
src/main/java/com/projects/task_master/
├── controllers/     # REST endpoints
├── services/        # Business logic
├── repositories/     # Spring Data JPA repositories
├── entities/         # JPA entities
├── dtos/
│   ├── requests/
│   └── responses/
├── mappers/          # MapStruct mappers
├── filters/           # JwtFilter
├── config/            # Security config
├── handlers/           # Global error handling + response wrappers
├── exceptions/          # Custom exceptions
├── enums/                # Roles, TaskStatus
└── utils/                 # JwtUtil
```

## Setup

1. Create a MySQL database (the app will auto-create `task-master` if it doesn't exist, given proper credentials).
2. Update `src/main/resources/application.properties` with your MySQL username/password if different from the defaults.
3. Set a real value for `jwt.secret.key` before deploying anywhere beyond your machine.
4. Run:
   ```
   ./mvnw spring-boot:run
   ```
5. The app starts on `http://localhost:8080`.

## API Overview

| Area | Endpoint | Method |
|---|---|---|
| Auth | `/api/auth/register` | POST |
| Auth | `/api/auth/signIn` | POST |
| Auth | `/api/auth/logout` | POST |
| Users | `/api/users/profile` | GET |
| Users | `/api/users/update` | POST |
| Users | `/api/users/delete/{id}` | DELETE (ADMIN) |
| Teams | `/api/teams/create` | POST |
| Teams | `/api/teams/invite/{teamName}/{email}` | POST |
| Teams | `/api/teams/accept/{token}` | POST |
| Teams | `/api/teams/get/{teamName}` | GET |
| Teams | `/api/teams/getInvites` | GET |
| Tasks | `/api/tasks/create` | POST |
| Tasks | `/api/tasks/update/{title}` | PUT |
| Tasks | `/api/tasks/delete/{taskName}` | DELETE |
| Tasks | `/api/tasks/get/{taskName}` | GET |
| Tasks | `/api/tasks/getAll` | GET |
| Tasks | `/api/tasks/assign/{taskName}/{teamId}/{userEmail}` | POST |
| Tasks | `/api/tasks/assigned` | GET |
| Tasks | `/api/tasks/updateStatus/{taskName}/{status}` | POST |
| Tasks | `/api/tasks/getAllAssigned` | GET |
| Tasks | `/api/tasks/comment/{userId}/{taskId}/{teamId}` | POST |
| Tasks | `/api/tasks/tasks/{taskId}/attachments` | POST |
| Notifications | `/api/teams/notifications/stream` | GET (SSE) |
| Notifications | `/api/teams/notifications/send/{teamId}/{recipientEmail}` | POST |
| Notifications | `/api/teams/notifications` | GET |
| Notifications | `/api/teams/notifications/{notificationId}/read` | POST |

All endpoints except `register` and `signIn` require an `Authorization: Bearer <token>` header.

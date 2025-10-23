# API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <jwt-token>
```

## Endpoints

### Authentication

#### POST /auth/login
Login with username and password.

**Request:**
```json
{
  "username": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "1",
    "username": "user@example.com",
    "email": "user@example.com",
    "role": "USER"
  }
}
```

### Tasks

#### GET /tasks
Get all tasks with optional pagination.

#### POST /tasks
Create a new task.

**Request:**
```json
{
  "title": "Complete project documentation",
  "description": "Write comprehensive API documentation",
  "status": "TODO",
  "priority": "HIGH",
  "assigneeId": "user-uuid"
}
```

#### PUT /tasks/{id}
Update an existing task.

#### DELETE /tasks/{id}
Delete a task by ID.

#### GET /tasks/status/{status}
Get tasks filtered by status (TODO, IN_PROGRESS, DONE).

#### GET /tasks/stats/count
Get task statistics by status.

## OpenAPI Documentation
Interactive API documentation is available at:
```
http://localhost:8080/swagger-ui.html
```
# Meeting Room Booking API

A REST API built with Java and Spring Boot for managing meeting rooms, users, and reservations.

This project simulates a real-world booking system, focusing on domain modeling, business rule validation, conflict prevention, and backend best practices.

---

## Features

### User Management
- Create users
- Partially update users using PATCH
- Search users by name
- Delete users

### Meeting Room Management
- Create meeting rooms with code, name, capacity, and status
- Retrieve rooms by code
- Delete rooms
- Automatically set ACTIVE as default status

### Booking Management
- Create bookings linked to users and meeting rooms
- Validate booking time intervals
- Ensure rooms are active before booking
- Prevent overlapping bookings
- Retrieve all bookings

---

## Business Rules

This project implements important reservation system rules:

- Booking start time must be earlier than end time
- Meeting rooms must be active to accept reservations
- Bookings cannot overlap in the same room
- Conflict validation follows the interval logic `[start, end)`

These validations simulate real scheduling systems used in corporate environments.

---

## Architecture

This project follows a layered architecture:

```text
Controller → Service → Repository → Database
```

### Layers

- **Controller** → exposes REST endpoints
- **Service** → business logic implementation
- **Repository** → persistence layer abstraction
- **Entity** → JPA domain models
- **DTOs** → request and response data transfer
- **Mapper** → entity ↔ DTO conversion
- **Exceptions** → domain exception handling

---

## Main Endpoints

### Users

```http
POST /users
PATCH /users/{id}
GET /users?name={name}
DELETE /users/{id}
```

---

### Meeting Rooms

```http
POST /meeting-rooms
GET /meeting-rooms/{code}
DELETE /meeting-rooms/{code}
```

---

### Bookings

```http
POST /bookings
GET /bookings
```

Booking validations:
- `start` must be before `end`
- room must be ACTIVE
- no overlapping reservations allowed

---

## Tech Stack

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL
- Gradle (Kotlin DSL)
- Docker
- Lombok
- JUnit
- Mockito

---

## Running the Project

### Clone repository

```bash
git clone https://github.com/biancapasch/meeting-room-booking-2.git
cd meeting-room-booking-2
```

---

### Start application

```bash
./gradlew bootRun
```

Application runs at:

```text
http://localhost:8080
```

---

## Running with Docker

```bash
docker compose up --build
```

---

## Running Tests

```bash
./gradlew test
```

---

## Future Improvements

- Pagination and filters
- Swagger/OpenAPI documentation
- Authentication and authorization
- Cloud deployment
- Integration tests

---

## Author

Bianca Paschoal  
GitHub: https://github.com/biancapasch

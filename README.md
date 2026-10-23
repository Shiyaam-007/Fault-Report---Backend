# Campus Fault Reporter — Backend API

A Spring Boot RESTful backend service for managing campus fault reporting, ticket tracking, image attachments, and role-based authentication.

---

## 🛠 Tech Stack

- **Java**: 25
- **Framework**: Spring Boot 4.1.1 (Spring Web, Spring Data JPA)
- **Database**: PostgreSQL (Neon Cloud / Local PostgreSQL)
- **Object-Relational Mapping (ORM)**: Hibernate
- **Build Tool**: Maven (`./mvnw`)
- **Containerization**: Docker

---

## 📂 Project Structure

```
backend/
├── src/main/java/com/sprint/backend/
│   ├── config/
│   │   ├── CorsConfig.java           # Cross-Origin Resource Sharing (CORS) setup
│   │   ├── DataInitializer.java     # Seeds default student and admin users
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── AuthController.java       # User authentication and registration
│   │   └── TicketController.java     # Ticket submission, retrieval, and status updates
│   ├── dto/
│   │   ├── CreateTicketRequest.java  # Form-data request DTO for ticket creation
│   │   ├── LoginRequest.java         # Sign-in request payload
│   │   ├── LoginResponse.java        # Authentication response payload
│   │   ├── RegisterRequest.java      # Registration payload
│   │   ├── TicketSummaryDto.java     # Lightweight summary of tickets (no raw bytea)
│   │   └── UpdateStatusRequest.java  # Ticket status update request
│   ├── entity/
│   │   ├── Photo.java                # Raw bytea image attachment entity
│   │   ├── Ticket.java               # Core ticket entity (status, description, reporter)
│   │   └── User.java                 # User entity with credentials (role: STUDENT, ADMIN)
│   ├── repository/
│   │   ├── PhotoRepository.java
│   │   ├── TicketRepository.java
│   │   └── UserRepository.java
│   ├── service/
│   │   └── TicketService.java        # Business logic for tickets and attachments
│   └── BackendApplication.java       # Application entry point
├── src/main/resources/
│   └── application.properties       # Spring application configuration
├── .env                             # Environment variables (Database credentials)
├── Dockerfile                       # Multi-stage Docker build
└── pom.xml                          # Maven build configuration
```

---

## ⚙️ Environment Configuration

Create or update `.env` in the `backend/` directory:

```properties
DATABASE_URL=postgresql://<HOST>:<PORT>/<DATABASE>?sslmode=require
DB_USER=<YOUR_DB_USERNAME>
DB_PASSWORD=<YOUR_DB_PASSWORD>
```

---

## 🚀 Running the Backend

### Prerequisites
- JDK 25 installed
- Maven (or use the included `./mvnw` wrapper)

### Development Mode
Navigate to the `backend` directory and start the server:

```bash
cd backend
./mvnw spring-boot:run
```

The server will start on `http://localhost:8080`.

### Building the JAR
```bash
./mvnw clean package
```

### Running with Docker
```bash
docker build -t campus-backend .
docker run -p 8080:8080 campus-backend
```

---

## 🔑 Default Credentials (Auto-Seeded)

The application automatically seeds default accounts on first startup:

| Role | Username | Password | Full Name |
| :--- | :--- | :--- | :--- |
| **Student** | `student` | `student123` | Alice Student |
| **Admin** | `admin` | `admin123` | Bob Admin |

---

## 📡 API Endpoints

### 1. Authentication (`/api/auth`)

- **`POST /api/auth/login`**
  - **Body**:
    ```json
    {
      "username": "student",
      "password": "student123"
    }
    ```
  - **Response (`200 OK`)**:
    ```json
    {
      "token": "uuid-token",
      "id": 1,
      "name": "Alice Student",
      "username": "student",
      "role": "STUDENT",
      "message": "Login successful"
    }
    ```

- **`POST /api/auth/register`**
  - Registers a new student account.
  - **Body**:
    ```json
    {
      "name": "John Doe",
      "username": "johndoe",
      "password": "password123"
    }
    ```

---

### 2. Tickets (`/api/tickets`)

- **`POST /api/tickets`**
  - Consumes: `multipart/form-data`
  - **Fields**:
    - `title` *(string, required)*: Brief summary of the problem.
    - `description` *(string, optional)*: Detailed description of the fault.
    - `reporterId` *(number, optional)*: User ID of the reporting student.
    - `image` *(file, optional)*: Photo of the issue (JPEG/PNG).
  - **Response (`201 Created`)**:
    ```json
    {
      "id": 12,
      "title": "Broken light",
      "description": "2nd floor stairwell",
      "status": "OPEN",
      "hasPhoto": true,
      "createdAt": "2026-10-23T11:40:00Z",
      "updatedAt": "2026-10-23T11:40:00Z"
    }
    ```

- **`GET /api/tickets`**
  - Returns all ticket summaries (no heavy image binary data).
  - **Query Parameters**:
    - `reporterId` *(optional)*: Filter tickets submitted by a specific student (e.g. `/api/tickets?reporterId=1`).

- **`GET /api/tickets/{id}/photo`**
  - Streams the raw image bytes with appropriate `Content-Type` header (used directly in `<img src="..." />`).

- **`PATCH /api/tickets/{id}/status`**
  - Updates the ticket status (`OPEN`, `IN_PROGRESS`, `RESOLVED`).
  - **Body**:
    ```json
    {
      "status": "IN_PROGRESS"
    }
    ```

# EventMate 🎯

Backend Event Management & Booking API built with Spring Boot 3, PostgreSQL, and JWT Authentication.

## 🏗 Setup Instructions

### 1. Clone & Build
```bash
git clone https://github.com/<your-username>/eventmate.git
cd eventmate
mvn clean install

```

### 2. Docker
```
docker compose down -v
docker compose pull
docker compose up -d
docker exec -it eventmate_db psql -U eventmate_user -d eventmate -c "\dt"
```

## Description

---

# **EventMate – Event Management & Booking API**

EventMate is a backend service built with Spring Boot that allows users to create, manage, discover, and register for events. It features role-based access control, JWT authentication, event capacity management, waitlisting, email notifications, and complete API documentation.

---

## **📌 Features**

* User registration & login with JWT authentication
* Roles: **ORGANIZER** and **ATTENDEE**
* Create, update, delete events (organizers only)
* Register, cancel, waitlist, and automatic promotion
* Email notifications via Mailgun/Sendinblue
* Filterable event listing (date range, location, organizer)
* Database migrations using Flyway
* Comprehensive testing: unit + integration (Testcontainers)
* Swagger-based API documentation
* Dockerized PostgreSQL setup

---

## **🎯 Learning Goals**

This project helps you master:

* Spring Boot fundamentals (DI, configs, profiles)
* Spring Data JPA + Hibernate relationships
* Spring Security (JWT)
* Validation, exception handling, ControllerAdvice
* Service-repository architecture
* Flyway migrations
* JUnit 5 + Mockito + Testcontainers
* Docker Compose for local development
* Optional external API integration (email services)

---

## **🧠 System Architecture**

### **Layered Architecture**

| Layer      | Responsibility  | Technology            |
| ---------- | --------------- | --------------------- |
| Controller | HTTP layer      | Spring Web MVC        |
| Service    | Business logic  | Spring Beans          |
| Repository | Database access | Spring Data JPA       |
| Database   | Persistence     | PostgreSQL            |
| Auth       | Security        | Spring Security + JWT |
| Migration  | Schema mgmt     | Flyway                |
| Build      | Packaging       | Maven                 |
| Deployment | Container       | Docker Compose        |

---

## **⚙️ Tech Stack**

| Category   | Technology                       |
| ---------- | -------------------------------- |
| Language   | Java 17+                         |
| Framework  | Spring Boot 3.x                  |
| Database   | PostgreSQL (Docker)              |
| ORM        | Hibernate                        |
| Migration  | Flyway                           |
| Validation | Jakarta Validation               |
| Auth       | Spring Security + JWT            |
| Testing    | JUnit 5, Mockito, Testcontainers |
| Docs       | Springdoc OpenAPI (Swagger)      |
| Email      | Mailgun or Sendinblue            |

---

## **📁 Repository Setup**

### **1. Initialize Project**

```bash
spring init --dependencies=web,data-jpa,security,validation,flyway,openapi,devtools \
--build=maven --java-version=17 eventmate
```

### **2. Git Setup**

```bash
cd eventmate
git init
git add .
git commit -m "Initial Spring Boot setup"
```

### **3. Docker Compose for PostgreSQL**

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    container_name: eventmate_db
    environment:
      POSTGRES_DB: eventmate
      POSTGRES_USER: eventmate_user
      POSTGRES_PASSWORD: eventmate_pass
    ports:
      - "5432:5432"
    volumes:
      - db_data:/var/lib/postgresql/data

volumes:
  db_data:
```

Run DB:

```bash
docker compose up -d
```

---

## **🧠 Functional Requirements**

### **1. User Management**

* Register (name, email, password, role)
* BCrypt password hashing
* Login returns JWT
* Role handling: ATTENDEE vs ORGANIZER

### **2. Event Management**

* CRUD operations (organizers only)
* Fields: id, title, description, location, startTime, endTime, capacity, createdBy
* Validation rules:

  * `endTime > startTime`
  * `capacity > 0`
  * No overlapping events for same organizer

### **3. Event Registration**

* Attendee registers for an event
* If full → auto-waitlist
* Cancellation → promote next waitlisted user
* Email notifications on registration, cancellation, promotion

### **4. Event Listing**

Public endpoints:

* Upcoming events (paginated)
* Filter: date range, organizer, location

### **5. Notifications**

* Integrate with Mailgun or Sendinblue (free tier)
* Trigger emails for key actions

---

## **✅ Acceptance Criteria**

### **General**

* Business logic stays strictly in service layer
* Controllers must never access repositories directly
* Swagger docs enabled
* Configurable via profiles:

  * `dev` → PostgreSQL
  * `test` → H2

---

### **Authentication & Security**

* JWT for secured routes
* `/api/auth/**` is public
* RBAC:

  * Organizer → manage events
  * Attendee → register/cancel

---

### **Persistence**

* Proper entity relationships:

  * One-to-Many: Organizer → Events
  * Many-to-Many: Event ↔ Attendee with extra fields
* Flyway migration scripts required

---

### **Validation & Error Handling**

* Use `@Valid` on inputs
* `@ControllerAdvice` for unified error format:

```json
{
  "timestamp": "...",
  "message": "...",
  "details": "..."
}
```

---

### **Testing**

* Service layer tests (Mockito)
* Controller tests (MockMvc)
* Integration tests (Testcontainers + PostgreSQL)
* Minimum **70% code coverage**

---

### **Documentation**

* Swagger at:
  **`/swagger-ui.html`**
* README includes setup, usage, and architecture details

---

## **🚀 Running the Application**

### **1. Start the database**

```bash
docker compose up -d
```

### **2. Run Spring Boot**

```bash
mvn spring-boot:run
```

### **3. Access Swagger**

```
http://localhost:8080/swagger-ui.html
```

---

## **📦 Build & Run with Docker**

### Build image:

```bash
docker build -t eventmate .
```

### Run:

```bash
docker run -p 8080:8080 eventmate
```

---

## **🧪 Running Tests**

### Unit Tests:

```bash
mvn test
```

### Integration Tests (Testcontainers):

```bash
mvn verify
```

---

## **📬 Email Integration Setup**

You must configure:

```
MAIL_API_KEY=
MAIL_DOMAIN=
MAIL_FROM=
```

Set them in:

```
application-dev.yml
```

---


### Testing Apis
```
technogise@Mac eventmate % curl -X POST http://localhost:8080/api/auth/register \
 -H "Content-Type: application/json" \
 -d '{"name":"Aashish","email":"ash@example.com","password":"secret123","role":"organizer"}'

{"id":1,"name":"Aashish","email":"ash@example.com","password":"$2a$10$pYzPrCe/T/oqwiL4Fg.8DeaSpR34SqhLiasHuZtk9WQAXseDdZLhu","role":"ORGANIZER"}%     


technogise@Mac eventmate % curl -X POST http://localhost:8080/api/auth/login \
 -H "Content-Type: application/json" \
 -d '{"email":"ash@example.com","password":"secret123"}'

{"token":"eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhc2hAZXhhbXBsZS5jb20iLCJyb2xlIjoiT1JHQU5JWkVSIiwiaWF0IjoxNzYzMDEwMDkxLCJleHAiOjE3NjMwMTM2OTF9.MSgZHMJ5yoBIFyUiqQ40KM0FKsMmWWnsl67c8O9FO1T8jua2oogPsaN2IvEbJK7l"}%                         


technogise@Mac eventmate % 

```
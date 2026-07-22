# CRM Spring Boot Application

A comprehensive Customer Relationship Management (CRM) system built with Spring Boot, featuring REST API endpoints for managing trainees, trainers, and training sessions.

## Table of Contents

- [Project Overview](#project-overview)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Docker Setup](#docker-setup)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Project Structure](#project-structure)

## Project Overview

This is a Spring Boot-based CRM system designed to manage:
- **Trainees**: User profiles for gym members
- **Trainers**: Fitness trainer profiles and assignments
- **Training Sessions**: Schedule and track training sessions

The application follows RESTful principles and includes comprehensive API documentation via Swagger/OpenAPI.

## Technology Stack

### Core Framework
- **Spring Boot**: 4.1.0
- **Java**: 25 (Java SE 25)
- **Gradle**: Build automation tool

### Database
- **PostgreSQL**: 15 (Alpine)
- **Liquibase**: Database migration management
- **Spring Data JPA**: ORM abstraction layer
- **Hibernate**: ORM implementation with PostgreSQL dialect

### Additional Libraries
- **Lombok**: 1.18.x (Annotation processor for reducing boilerplate)
- **MapStruct**: 1.6.2 (Object mapping)
- **AspectJ**: 1.9.25.1 (AOP implementation)
- **SpringDoc OpenAPI**: 3.0.3 (API documentation)
- **Spring Dotenv**: 4.0.0 (Environment variable management)
- **Jakarta Validation**: 4.0.0-M1 (Bean validation)

### Testing
- **JUnit 5**: Platform launcher for test execution
- **Spring Boot Test**: Integration and unit testing utilities
- **TestContainers** (implied): For containerized testing environments

### Dependency Management
- **Spring Dependency Management**: 1.1.7
- **Spring Boot Plugin**: 4.1.0

## Prerequisites

Before setting up the project, ensure you have the following installed:

1. **Java Development Kit (JDK) 25**
   - Download from: https://www.oracle.com/java/technologies/
   - Verify installation: `java -version`

2. **Git**
   - Download from: https://git-scm.com/

3. **Docker & Docker Compose**
   - Download from: https://www.docker.com/products/docker-desktop
   - Verify installation: `docker --version` and `docker-compose --version`

4. **IDE (Optional but recommended)**
   - IntelliJ IDEA Community or Ultimate Edition
   - Eclipse or VS Code with Java extensions

## Installation & Setup

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd crm-spring-boot
```

### Step 2: Verify Java Installation

```bash
java -version
# Should output Java 25
```

### Step 3: Build the Project

Using Gradle wrapper (recommended):

```bash
# Windows
gradlew.bat clean build

# macOS/Linux
./gradlew clean build
```

Or with installed Gradle:

```bash
gradle clean build
```

### Step 4: Set Up Environment Variables

Create a `.env` file in the project root directory:

```env
# Database Configuration
DB_NAME=gym_crm
DB_USER=postgres
DB_PASSWORD=your_secure_password_here
```

**Important**: Never commit the `.env` file to version control. Add it to `.gitignore`.

### Step 5: Start PostgreSQL Database

```bash
docker-compose up -d postgres
```

This will:
- Pull PostgreSQL 15 Alpine image
- Create a container named `gym_crm_spring_boot_db`
- Expose the database on localhost:5432
- Create a persistent volume for data

Verify the database is running:

```bash
docker-compose ps
```

## Configuration

### Application Properties

The application is configured via `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/gym_crm
    username: postgres
    password: ${DB_PASSWORD}  # Set via .env file
    driver: org.postgresql.Driver
    pool:
      size: 10

  hibernate:
    dialect: org.hibernate.dialect.PostgreSQLDialect
    show_sql: true
    format_sql: true
    hbm2ddl:
      auto: validate
  
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.yaml
  
  logging:
    pattern:
      console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - [txn: %X{transactionId}] - %msg%n"
    level:
      com.gym.crmspringboot: INFO
```

### Key Configuration Points

- **Database Connection**: Configured for PostgreSQL on localhost:5432
- **Hibernate Validation Mode**: Set to `validate` (requires existing schema)
- **Liquibase Migrations**: Managed through `db/changelog/db.changelog-master.yaml`
- **Connection Pool**: 10 concurrent connections
- **Logging**: JSON-formatted logs with transaction IDs

## Running the Application

### Method 1: Using Gradle

```bash
# Windows
gradlew.bat bootRun

# macOS/Linux
./gradlew bootRun
```

### Method 2: Running the JAR

```bash
# Build the project first
gradle build

# Run the generated JAR
java -jar build/libs/crm-spring-boot-0.0.1-SNAPSHOT.jar
```

### Method 3: Using IDE

- **IntelliJ IDEA**: 
  - Right-click on the main class and select "Run"
  - Or use the Gradle task runner (View → Tool Windows → Gradle)

The application will start on `http://localhost:8080` by default.

## Docker Setup

### Running with Docker Compose

To run the entire stack (PostgreSQL + Application) with Docker:

1. **Build the Docker image** (ensure a Dockerfile exists):

```bash
docker build -t crm-spring-boot:latest .
```

2. **Update docker-compose.yml** to include the application service:

```yaml
services:
  postgres:
    image: postgres:15-alpine
    # ... existing config ...
  
  app:
    build: .
    depends_on:
      - postgres
    ports:
      - "8080:8080"
    environment:
      - DB_NAME=${DB_NAME}
      - DB_USER=${DB_USER}
      - DB_PASSWORD=${DB_PASSWORD}
    restart: unless-stopped
```

3. **Start the full stack**:

```bash
docker-compose up -d
```

4. **Check logs**:

```bash
docker-compose logs -f app
```

5. **Stop the stack**:

```bash
docker-compose down
```

### Database Cleanup

To completely remove the database and start fresh:

```bash
docker-compose down -v  # -v removes volumes
```

## API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

The API documentation is automatically generated using SpringDoc OpenAPI 3.0.3.

## Testing

### Run All Tests

```bash
# Windows
gradlew.bat test

# macOS/Linux
./gradlew test
```

### Run Specific Test Class

```bash
gradle test --tests TraineeControllerTest
```

### View Test Results

Test reports are generated in: `build/reports/tests/test/index.html`

### Available Test Suites

- `TraineeControllerTest` - Trainee endpoint tests
- `TrainerControllerTest` - Trainer endpoint tests
- `TrainingControllerTest` - Training session tests
- `UserControllerTest` - User management tests
- `CredentialsServiceTest` - Authentication service tests
- `TraineeServiceImplTest` - Trainee business logic
- `TrainerServiceImplTest` - Trainer business logic
- `TrainingServiceImplTest` - Training business logic
- `UserServiceImplTest` - User business logic

## Project Structure

```
crm-spring-boot/
├── src/
│   ├── main/
│   │   ├── java/com/gym/crmspringboot/
│   │   │   ├── controller/        # REST API endpoints
│   │   │   ├── service/           # Business logic
│   │   │   ├── repository/        # Data access layer
│   │   │   ├── entity/            # JPA entities
│   │   │   ├── dto/               # Data transfer objects
│   │   │   ├── mapper/            # MapStruct mappers
│   │   │   ├── exception/         # Custom exceptions
│   │   │   └── config/            # Spring configuration
│   │   └── resources/
│   │       ├── application.yaml   # Application configuration
│   │       └── db/changelog/      # Liquibase migrations
│   └── test/
│       └── java/com/gym/crmspringboot/
│           ├── controller/        # Controller tests
│           └── service/           # Service tests
├── build.gradle                   # Gradle configuration
├── docker-compose.yml             # Docker Compose setup
├── Dockerfile                     # Docker image definition
└── README.md                      # This file
```
---

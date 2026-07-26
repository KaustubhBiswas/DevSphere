# DevSphere

DevSphere is a developer collaboration platform built with Java and Spring Boot. The project is designed as a solid monolithic foundation for authentication, team management, project tracking, task workflows, issue handling, and future collaboration features.

## Overview

DevSphere aims to provide a centralized system for:

- user authentication and authorization
- organization and project management
- task and issue tracking
- extensible APIs for future dashboard and notification features

## Current Status

The project currently includes:

- a Spring Boot application scaffold
- authentication endpoints for user registration and login
- common response and exception handling utilities
- PostgreSQL-backed persistence configuration
- a basic web entry point for the application

## Tech Stack

- Java 26
- Spring Boot 4.1.0
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- Maven

## Project Structure

The source code is organized into domain-focused packages under the main application package:

- auth: authentication controllers, DTOs, and services
- common: shared exception handling and response wrappers
- config: application and security configuration
- organization, project, task, issue, notification, and user: planned domain modules

## Prerequisites

Before running the project, make sure you have:

- JDK 26 installed
- Maven 3.9+ available
- PostgreSQL running locally

## Getting Started

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd devsphere
   ```

2. Create a PostgreSQL database named `DevSphere`.

3. Update database credentials in `src/main/resources/application.yaml` if needed.

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

5. Open the application in your browser at:
   ```text
   http://localhost:8080
   ```

## API Endpoints

The current backend exposes basic authentication endpoints:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /`

## Build and Test

Run the test suite with:

```bash
./mvnw test
```

## Roadmap and Documentation

Project planning and feature roadmaps are available in the `docs/` directory:

- `docs/DevSphere_Development_Roadmap.md`
- `docs/DevSphere_Feature_Roadmap.md`

## Contributing

Contributions are welcome. If you plan to make substantial changes, it is a good idea to open an issue first so the direction of the project can be discussed.
